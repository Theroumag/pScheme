# Scheme Interpreter (pScheme) - Core Interpreter Implementation
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)  ![Scheme](https://img.shields.io/badge/scheme-%235EAA62.svg?style=for-the-badge&logo=scheme&logoColor=white)  

This project implements the core interpreter for a Scheme language in Java. It provides a complete environment for evaluating Scheme expressions, including arithmetic operations, boolean logic, list manipulation, and function definitions. The implementation includes a robust REPL (Read-Eval-Print Loop) that can process both file input and interactive commands.

## Course Information  
**Course:** CIS 443 - Programming Languages  
**Assignment:** pScheme  
**Due Date:** May 9th, 2025  

## Implementation

The interpreter includes:

1. **Global Environment**:
   - Arithmetic operations: `+`, `-`, `*`, `/`, `%`
   - Boolean operations: `not`, `and`, `or`
   - List operations: `car`, `cdr`, `cons`, `null?`
   - Comparison operators: `=`, `eq?`, `eqv?`, `equal?`
   - Special forms: `define`, `lambda`, `if`, `quote`

2. **REPL Driver**:
   - Supports both file input and interactive mode
   - Handles errors gracefully with informative messages
   - Maintains expression count and provides formatted output

## Running the Interpreter

Use Gradle to compile and run:

```bash
gradle build
gradle run --quiet --console=plain
```

### To Run with a File:
```bash
gradle run --console=plain --args="test.scm"
```

### To Run in Interactive Mode:
```bash
gradle run --console=plain
```

The interpreter will start in interactive mode if no file is specified, showing a prompt like:
```
[1] +-> 
```

## Example Session

```
========================================
REPL Driver 0.5-Scheme
========================================
[1] +-> (+ 1 2)
  3
[2] +-> (define x 5)
  ()
[3] +-> (* x 2)
  10
```

## Error Handling

The interpreter provides clear error messages for:
- Type mismatches
- Undefined variables
- Syntax errors
- Runtime errors

All errors are caught and displayed without crashing the interpreter, allowing for continuous operation in the REPL.



This output corresponds to the evaluation of each expression in `test.scm` in sequence, demonstrating the correct behavior of:
- Number literals and arithmetic
- Variable definitions and references
- Boolean operations
- List operations
- Function definitions and applications
- Control flow
- Quoted expressions


## Test Cases

The `test.scm` file demonstrates the core functionality of the interpreter:

### Test Categories:
1. **Basic Arithmetic**:
   ```scheme
   (+ 1 2)
   (- 5 3)
   (* 4 3)
   (/ 10 2)
   (% 7 3)
   ```

2. **Boolean Operations**:
   ```scheme
   (not #t)
   (and #t #t)
   (or #f #t)
   ```

3. **List Operations**:
   ```scheme
   (cons 1 2)
   (car (cons 1 2))
   (cdr (cons 1 2))
   (null? ())
   ```

4. **Function Definition and Application**:
   ```scheme
   (define square (lambda (x) (* x x)))
   (square 5)
   ```

5. **Control Flow**:
   ```scheme
   (if #t 1 2)
   (if (= 5 8) 3 (+ 7 2))
   ```

## Expected Test Output

When running `test.scm`, you should see the following output organized by test category:

### 1. Basic Arithmetic
```
1
-10
3
2
```

### 2. Variable Definitions and References
```
x
9
a
b
30
200
3
2
12
5
1
3
```

### 3. Boolean Operations
```
#f
#t
#t
#f
#t
#f
4
```

### 4. List Operations
```
(1 . 2)
1
2
#t
#f
#t
#f
#t
#f
5
```

### 5. Comparison Operations
```
#t
#f
#t
#f
#t
#f
6
```

### 6. Function Definition and Application
```
square
25
7
```

### 7. Control Flow
```
1
2
8
```

### 8. List Construction and Manipulation
```
(1 2 3)
(1 2 3)
9
11
```
