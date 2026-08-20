# Bingus UI Test Plan

All divider lines begin at column one. Ordinary response lines have four leading spaces, and newly added task lines have eight.

Before Tests 1–4, ensure that `data/bingus.txt` does not exist. This keeps their saved task data independent.

## Test 1: Add a todo task

### Input

```text
todo borrow book
bye
```

### Expected output

```text
____________________________________________________________
    Got it. I've added this task:
        [T][ ] borrow book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```

## Test 2: Add deadline and event tasks

### Input

```text
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
bye
```

### Expected output

```text
____________________________________________________________
    Got it. I've added this task:
        [D][ ] return book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
        [E][ ] project meeting
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```

## Test 3: List and mark mixed task types

### Input

```text
todo read book
deadline return book /by June 6th
event project meeting /from Aug 6th 2pm /to 4pm
list
mark 2
list
bye
```

### Expected output

```text
____________________________________________________________
    Got it. I've added this task:
        [T][ ] read book
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
        [D][ ] return book
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
        [E][ ] project meeting
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book
    3.[E][ ] project meeting
____________________________________________________________
____________________________________________________________
    Nice! I've marked this task as done : ) 
        [D][X] return book
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book
    3.[E][ ] project meeting
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```

## Test 4: Persist task text containing the record delimiter

### Input

```text
todo plan | revise
bye
```

### Expected output

```text
____________________________________________________________
    Got it. I've added this task:
        [T][ ] plan | revise
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```

After the first command, verify that `data/bingus.txt` contains:

```text
T|0|cGxhbiB8IHJldmlzZQ==
```

## Test 5: Load saved task records at startup

### Setup

Create `data/bingus.txt` with:

```text
T|1|cGxhbiB8IHJldmlzZQ==
```

### Input

```text
list
bye
```

### Expected output

```text
____________________________________________________________
    Here are the tasks in your list:
    1.[T][X] plan | revise
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```

## Test 6: Start safely with a corrupted save file

### Setup

Create `data/bingus.txt` with this invalid content:

```text
T|1|not-valid-base64!
```

### Input

```text
list
bye
```

### Expected output

```text
    I couldn't load your saved tasks. Starting with an empty list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
____________________________________________________________
____________________________________________________________
    Bye! Visit me again when you're free :) 
____________________________________________________________
```
