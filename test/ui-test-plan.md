# bingus.Bingus UI Test Plan

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
    Bye! Hope you visit me again :> 
____________________________________________________________
```

## Test 2: Add deadline and event tasks

### Input

```text
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
bye
```

### Expected output

```text
____________________________________________________________
    Got it. I've added this task:
        [D][ ] return book (by: Dec 2 2019, 6:00 pm)
    Now you have 1 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
        [E][ ] project meeting (from: Dec 3 2019, 2:00 pm to: Dec 3 2019, 4:00 pm)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Bye! Hope you visit me again :> 
____________________________________________________________
```

## Test 3: List and mark mixed task types

### Input

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-03 1400 /to 2019-12-03 1600
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
        [D][ ] return book (by: Dec 2 2019, 6:00 pm)
    Now you have 2 tasks in the list.
____________________________________________________________
____________________________________________________________
    Got it. I've added this task:
        [E][ ] project meeting (from: Dec 3 2019, 2:00 pm to: Dec 3 2019, 4:00 pm)
    Now you have 3 tasks in the list.
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
    3.[E][ ] project meeting (from: Dec 3 2019, 2:00 pm to: Dec 3 2019, 4:00 pm)
____________________________________________________________
____________________________________________________________
    Nice! I've marked this task as done : ) 
        [D][X] return book (by: Dec 2 2019, 6:00 pm)
____________________________________________________________
____________________________________________________________
    Here are the tasks in your list:
    1.[T][ ] read book
    2.[D][X] return book (by: Dec 2 2019, 6:00 pm)
    3.[E][ ] project meeting (from: Dec 3 2019, 2:00 pm to: Dec 3 2019, 4:00 pm)
____________________________________________________________
____________________________________________________________
    Bye! Hope you visit me again :> 
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
    Bye! Hope you visit me again :> 
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
    Bye! Hope you visit me again :> 
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
    Bye! Hope you visit me again :> 
____________________________________________________________
```

## Test 7: Validate date and time input

### Input

```text
deadline return book /by 2019-02-29 1800
event meeting /from 2019-12-03 1600 /to 2019-12-03 1400
bye
```

### Expected output

```text
____________________________________________________________
    Invalid deadline date/time. Please use yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.
____________________________________________________________
____________________________________________________________
    Event end date/time must be after its start date/time.
____________________________________________________________
____________________________________________________________
    Bye! Hope you visit me again :> 
____________________________________________________________
```

## Test 8: List tasks occurring on a date

### Setup

Create these tasks before running the command:

```text
todo read book
deadline return book /by 2019-12-02 1800
event project meeting /from 2019-12-02 1400 /to 2019-12-03 1600
```

### Input

```text
list 2019-12-02
bye
```

### Expected output

```text
____________________________________________________________
    Here are the tasks on Dec 2 2019:
    2.[D][ ] return book (by: Dec 2 2019, 6:00 pm)
    3.[E][ ] project meeting (from: Dec 2 2019, 2:00 pm to: Dec 3 2019, 4:00 pm)
____________________________________________________________
____________________________________________________________
    Bye! Hope you visit me again :> 
____________________________________________________________
```
