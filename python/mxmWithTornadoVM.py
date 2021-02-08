#!/usr/bin/python

print("Running with tornadoVM")
import java
import time
myclass = java.type('uk.ac.manchester.tornado.examples.polyglot.MyCompute')

start = time.time()
output = myclass.computeMxM()
end = time.time()
print(output.toString())
print("Total time: " + str((end-start)))
