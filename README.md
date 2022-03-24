# ActiveObject

Consumer Producer problem solved using Active Object design pattern.



Consumers, producers and buffer each use separate threads. Consumers and 
producers make requests through the interface created in class Proxy. 
Requests are put into ActivationQueue and after being called by 
Scheduler they are processed in Servant. Future mechanism is implemented 
so that Consumer and Producer can do tasks in background while waiting 
for the Requests to be processed.


