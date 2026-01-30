A Java-based simulation of core Operating System concepts, built for an Operating Systems course.
This project models how a kernel manages processes, memory, and devices at a conceptual level.

 Overview

This project simulates how an OS kernel interacts with user processes, schedules execution, manages memory, and communicates with devices — all implemented in Java for learning and experimentation.

The goal was not to build a real OS, but to deeply understand and implement:

• Process scheduling
• Virtual memory concepts
• System calls
• Device abstraction
• Kernel ↔ Userland separation

Concepts Implemented

 Process Control Blocks (PCB)

 Round-robin scheduling with time slicing

Virtual memory w/ page tables & TLB

 System calls: open, read, write, close

 Virtual File System (VFS)

Simulated devices (RandomDevice, FakeFileSystem)

 Kernel/Userland boundary modeling
