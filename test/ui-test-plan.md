# Bingus UI Test Plan

Use the sample's indentation exactly: four spaces before each divider, five spaces before ordinary response lines, and seven spaces before a newly added task line.

```text
    ____________________________________________________________
```

Expected output is the response to each command. The startup greeting is included in the recorded console transcript but is not repeated in every expected-output block.

## Test 1: Add a todo task

Aim: Verify that `todo` creates, displays, and counts an incomplete Todo.

### Input

```text
todo borrow book
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] borrow book
     Now you have 1 tasks in the list.
    ____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
    ____________________________________________________________
     Bye! Visit me again when you're free :)
    ____________________________________________________________
```

## Test 2: Add deadline and event tasks

Aim: Verify deadline and event formatting.

### Input

```text
deadline return book /by Sunday
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: Sunday)
     Now you have 1 tasks in the list.
    ____________________________________________________________
```

### Input

```text
event project meeting /from Mon 2pm /to 4pm
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Mon 2pm to: 4pm)
     Now you have 2 tasks in the list.
    ____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
    ____________________________________________________________
     Bye! Visit me again when you're free :)
    ____________________________________________________________
```

## Test 3: List and mark mixed task types

Aim: Verify polymorphic listing and marking a deadline as complete.

### Input

```text
todo read book
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] read book
     Now you have 1 tasks in the list.
    ____________________________________________________________
```

### Input

```text
deadline return book /by June 6th
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [D][ ] return book (by: June 6th)
     Now you have 2 tasks in the list.
    ____________________________________________________________
```

### Input

```text
event project meeting /from Aug 6th 2pm /to 4pm
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
     Now you have 3 tasks in the list.
    ____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][ ] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________
```

### Input

```text
mark 2
```

### Expected output

```text
    ____________________________________________________________
     Nice! I've marked this task as done:
       [D][X] return book (by: June 6th)
    ____________________________________________________________
```

### Input

```text
list
```

### Expected output

```text
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][ ] read book
     2.[D][X] return book (by: June 6th)
     3.[E][ ] project meeting (from: Aug 6th 2pm to: 4pm)
    ____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
    ____________________________________________________________
     Bye! Visit me again when you're free :)
    ____________________________________________________________
```

## Test 4: Persist task text containing the record delimiter

Aim: Verify that task changes are written to `data/bingus.txt` and a description containing `|` remains intact.

### Input

```text
todo plan | revise
```

### Expected output

```text
    ____________________________________________________________
     Got it. I've added this task:
       [T][ ] plan | revise
     Now you have 1 tasks in the list.
    ____________________________________________________________
```

After this command, verify that `data/bingus.txt` contains this record:

```text
T|0|cGxhbiB8IHJldmlzZQ==
```

### Input

```text
bye
```

### Expected output

```text
    ____________________________________________________________
    Bye! Visit me again when you're free :)
    ____________________________________________________________
```

## Test 5: Load saved task records at startup

Aim: Verify that a completed todo task saved in `data/bingus.txt` is restored when Bingus starts.

### Setup

Create `data/bingus.txt` with this content before starting Bingus:

```text
T|1|cGxhbiB8IHJldmlzZQ==
```

### Input

```text
list
```

### Expected output

```text
    ____________________________________________________________
     Here are the tasks in your list:
     1.[T][X] plan | revise
    ____________________________________________________________
```

### Input

```text
bye
```

### Expected output

```text
    ____________________________________________________________
    Bye! Visit me again when you're free :)
    ____________________________________________________________
```

## Test 6: Start safely with a corrupted save file

Aim: Verify that Bingus reports a corrupted save file and starts with an empty task list.

### Setup

Create `data/bingus.txt` with this invalid content before starting Bingus:

```text
T|1|not-valid-base64!
```

On startup, Bingus should display:

```text
    I couldn't load your saved tasks. Starting with an empty list.
```

### Input

```text
list
```

### Expected output

```text
    ____________________________________________________________
    Here are the tasks in your list:
    ____________________________________________________________
```
