# python -m pip install mariadb sqlalchemy
"""
from repositories.role_repository import get_roles
from repositories.user_repository import get_users, create_user,change_pass,delete_user,login,add_role

if __name__ == "__main__":

    print("Creating user:")
    new_user = create_user("test", "test")
    print(new_user)
    add_role("test","client")
    print("\nUsers:")
    for user in get_users():
        print(f"{user.user_id} - {user.username} - {user.password} - roles: ", end="")
        for role in user.roles:
            print(f"{role.role_name} ", end="")
        print()

    print("\n\nRoles:")
    for role in get_roles():
        print(f"{role.role_name}")

#    change_pass("test","test","ioana")
 #   delete_user("test","ioana")
    print("\nUsers:")

    for user in get_users():
        print(f"{user.user_id} - {user.username} - {user.password} - roles: ", end="")
        for role in user.roles:
            print(f"{role.role_name} ", end="")
        print()
   # print(login("ioana","ioana"))

"""
# python -m pip install lxml spyne
from spyne import Application, rpc, ServiceBase, Integer, Double, String
from spyne.protocol.soap import Soap11
from spyne.server.wsgi import WsgiApplication
import logging
from wsgiref.simple_server import make_server

from jwt import login_for_access_token, authorize
from repositories.user_repository import create_user, get_users, change_pass, delete_user, login, add_role


class CalculatorService(ServiceBase):
    @rpc(String, String, _returns=String)
    def create_user(self, username, password):
        return create_user(username, password)

    @rpc(_returns=String)
    def get_users(self):
        return get_users()

    @rpc(String, String, String,String, _returns=String)
    def change_pass(self, username, password, new_pass,jwt):
        return change_pass(username, password, new_pass,jwt)

    @rpc(String, String,String, _returns=String)
    def delete_user(self, username, password,jwt):
        return delete_user(username, password, jwt)

    @rpc(String, String, _returns=String)
    def login(self, username, password):
        return login_for_access_token(username, password)

    @rpc(String, String, _returns=String)
    def add_role(self, username, role_name):
        return add_role(username, role_name)

    @rpc(String, _returns=String)
    def authorize(self, token):
        return authorize(token)

    @rpc(String, _returns=String)
    def logout(self, jwt):
        response = ""
        try:
            f = open("repositories/blacklist.txt", "a")
            f.write(jwt + "\n")
            response = "True"
            f.close()
        except:
            response = "False"
        return response


application = Application([CalculatorService], 'services.dbManager.soap',
                          in_protocol=Soap11(validator='lxml'),
                          out_protocol=Soap11())

wsgi_application = WsgiApplication(application)

if __name__ == '__main__':
    logging.basicConfig(level=logging.INFO)
    logging.getLogger('spyne.protocol.xml').setLevel(logging.INFO)

    logging.info("listening to http://127.0.0.1:8000")
    logging.info("wsdl is at: http://127.0.0.1:8000/?wsdl")

    server = make_server('127.0.0.1', 8000, wsgi_application)
    server.serve_forever()
