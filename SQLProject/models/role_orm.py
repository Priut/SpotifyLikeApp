from sqlalchemy import Column, String, Integer
from base.sql_base import Base


class Role(Base):
    __tablename__ = 'roles'

    role_id = Column(Integer, primary_key=True)
    role_name = Column(String)

    def __init__(self, role_name):
        self.role_name = role_name
