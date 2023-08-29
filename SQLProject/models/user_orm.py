from sqlalchemy import Column, String, Integer
from base.sql_base import Base
from sqlalchemy.orm import relationship
from models.users_roles_orm import joinUR


class User(Base):
    __tablename__ = 'users'

    user_id = Column(Integer, primary_key=True)
    username = Column(String)
    password = Column(String)
    roles = relationship("Role", secondary=joinUR)#secondary=tabelul de join

    def __init__(self, username, password):
        self.username = username
        self.password = password
