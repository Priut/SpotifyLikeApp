from jwt import validate_jwt
from models.user_orm import User
from base.sql_base import Session
from models.role_orm import Role
from models.users_roles_orm import joinUR

from sqlalchemy import delete


def get_users():
    session = Session()
    users = session.query(User).all()
    user_string = ""
    for user in users:
        user_string = user_string + "\n " + str(
            user.user_id) + " - " + user.username + " - " + user.password + " - roles: "
        for role in user.roles:
            user_string = user_string + role.role_name + " "

    return user_string


def create_user(username, password):
        session = Session()
        user = User(username, password)
        try:
            session.add(user)
            session.commit()
            return f"User added succesfully - {user}"
        except Exception as exc:
            return f"Failed to add user - {exc}"


def change_pass(username, password, new_pass, jwt):
    if validate_jwt(jwt):
        session = Session()
        try:
            user = session.query(User).filter(User.username == username and User.password == password).update(
                {'password': new_pass})
            session.commit()
            return "Password changed"
        except Exception as exc:
            return f"Failed to change password - {exc}"
    else:
        return "Error! Invalid JWT"


def delete_user(username, password, jwt):
    if validate_jwt(jwt):
        session = Session()
        user_id = session.query(User).filter(User.username == username)[0].user_id
        try:

            print(user_id)
            stmt = joinUR.delete(FK_users=user_id)
            # stmt="DELETE FROM joinUR WHERE FK_users = "+ str(user_id)+";"
            session.execute(stmt)
            session.commit()

            session.query(User).filter(User.username == username and User.password == password).delete()
            session.commit()
            return "User deleted"
        except Exception as exc:
            return f"Failed to delete user - {exc}"
    else:
        return "Error! Invalid JWT"


def login(username, password):
    session = Session()
    user = session.query(User).filter(User.username == username and User.password == password)
    if user.count() != 0:
        return str(True)
    else:
        return str(False)


def add_role(username, role_name):
        session = Session()
        user_id = session.query(User).filter(User.username == username)[0].user_id
        role_id = session.query(Role).filter(Role.role_name == role_name)[0].role_id
        try:
            stmt = joinUR.insert().values(FK_users=user_id, FK_roles=role_id)
            session.execute(stmt)
            session.commit()
            return "Role inserted"
        except Exception as exc:
            return f"Failed to insert role - {exc}"
