from sqlalchemy import Column, String, Integer, Date, Table, ForeignKey

from base.sql_base import Base

joinUR = Table(
    'joinUR', Base.metadata,
    Column('FK_users', Integer, ForeignKey('users.user_id')),
    Column('FK_roles', Integer, ForeignKey('roles.role_id')),
    extend_existing=True
)
