

import numpy as np
import time 

size = 512
a = np.random.rand(size, size)
b = np.random.rand(size, size)

for i in range(10):
	start = time.time()
	np.dot(a, b)
	total = time.time() - start
	print(total)



