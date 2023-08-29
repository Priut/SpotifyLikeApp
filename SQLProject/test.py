# python -m pip install suds
from suds.client import Client
c = Client('http://localhost:8000/?wsdl')

print(c.service.login("iulian","iulian"))