from datetime import datetime, timedelta
from typing import Union

from fastapi import Depends, FastAPI, HTTPException, status
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import JWTError, jwt, ExpiredSignatureError
from passlib.context import CryptContext
from pydantic import BaseModel

from models.user_orm import User
from base.sql_base import Session
from models.role_orm import Role
from models.users_roles_orm import joinUR

# to get a string like this run:
# openssl rand -hex 32
SECRET_KEY = "09d25e094faa6ca2556c818166b7a9563b93f7099f6f0f4caa6cf63b88e8d3e7"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 30


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    username: Union[str, None] = None


pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")


def get_user(username: str):
    session = Session()
    user = session.query(User).filter(User.username == username)[0]
    if user:
        return user


def authenticate_user(username: str, password: str):
    user = get_user(username)
    if not user:
        return False
    if password != user.password:
        return False
    return user


def create_access_token(data: dict, expires_delta: Union[timedelta, None] = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


def login_for_access_token(username, password):
    user = authenticate_user(username, password)
    if not user:
        return "Error! Incorect credentials"
    access_token_expires = timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES)
    access_token = create_access_token(
        data={"sub": str(user.user_id)}, expires_delta=access_token_expires
    )
    return access_token


def authorize(token):
    f = open("repositories/blacklist.txt", "r")
    content = f.readlines()
    for jwts in content:
        if token == jwts[:-1]:
            return "Error! Invalid JWT"
    try:
        decoded_token = jwt.decode(token, SECRET_KEY, algorithms=ALGORITHM)
    except ExpiredSignatureError:
        return "Error! Expired JWT"
    id = decoded_token["sub"]
    session = Session()
    user = session.query(User).filter(User.user_id == id)[0]
    return_string = ""
    if user:
        return_string = str(user.user_id)
        for role in user.roles:
            return_string = return_string + "||" + role.role_name
        return return_string


def validate_jwt(token):
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            return False
        token_data = TokenData(username=username)
    except JWTError:
        return False
    user = get_user(username=token_data.username)
    if user is None:
        return False
    return True
