# Coding up the algo used by elevator in my building
There was a power cut in my building, so I couldn’t access the internet for a few hours. Since I couldn’t work on anything else, I decided to figure out how the elevator in the building works and try coding the algorithm it probably uses. Here's how I broke it down and pieced it together.

- once the car starts going up, it don’t reverse until all "up" requests are served. Same for down. So keeping a `currentDirection` helps guide decisions.

- requests from both inside the lift and hallways need to be tracked. Internally, queues make sense:

*   an `upQueue` (min-heap) for floors in increasing order.
*   a `downQueue` (max-heap) for decreasing floors.
*   sets help deduplicate floors since buttons can be pressed multiple times.

##

- for **car requests** if someone inside the lift presses floor 8 while on floor 2, that's an upward request. Can be added to `upQueue`. If lift is idle, this request can set the direction.



- **hall requests** are slightly trickier.  if a person at floor 3 presses the DOWN button:

*   If the lift is going up and hasn’t reached floor 3 yet, it should skip the request for now.
*   That floor can be queued for downwards movement later.
*   Direction of the request should match the current direction and be on the way for the lift to serve it immediately.

## 

- Lift should continue in the same direction until its queue is empty, then reverse if needed. Only go idle when no requests are pending in either direction.

- Priority queues help always pick the nearest floor in the current direction.

