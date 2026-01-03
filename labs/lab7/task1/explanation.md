## 1. `getTextCounter` Method
```java
return () -> {
    // Computation happens here, inside the Callable
    // Count lines, words, and characters
    return new Counter(textId, lineCount, wordCount, charCount);
};
```
- Uses lambda expression `() -> { ... }` to create a `Callable<Counter>`
- The lambda has no parameters and returns a `Counter`
- All counting logic is **inside** the callable (not computed before)

## 2. Adding Tasks to the List
```java
Callable<Counter> task = getTextCounter(textId, text.toString());
tasks.add(task);
```
- Creates a callable for each text read from the input
- Adds it to the `tasks` list

## 3. Executing tasks Concurrently
```java
List<Future<Counter>> futures = executor.invokeAll(tasks);
```
- `invokeAll()` executes all callables and returns a list of futures
- Task run concurrently using the thread pool

## 4. Extracting Results
```java
for (Future<Counter> future : futures) {
    Counter counter = future.get();
    results.add(counter);
}
```
- `future.get()` retrieves the `Counter` result from each future
- Blocks until the result is available

## Example Input/Output

**Input:**
```
2
1
3
Hello world
This is a test
End of text
2
2
Single line
Another line
```

**Output:**
```
1 3 9 40
2 2 4 23
```

## 5. What the limit parameter does:
```java
String.split(String regex, int limit)
```
The `limit` parameter controls:
1. **How many times** the pattern is applied
2. Whether **trailing empty strings are included** in the limit

### Three cases:
#### 1. Positive limit (e.g, `split("\n", 2)`)
- Splits at most `limit - 1` times
- Array has at most `limit` elements

#### 2. Zero limit (e.g., `split("\n", 0)` or just `split("\n")`)
- Splits as many times as possible
- **Trailing empty string are REMOVED**

#### 3. Negative limit (e.g., `split("\n", -1)`)
- Splits as many times as possible
- **Trailing empty strings are PRESERVED**

### Example:
```java
String text1 = "a\nb\nc";
String text2 = "a\nb\n";   // ends with newline
String text3 = "a\nb\n\n"; // ends with two newlines

// Without -1 (trailing empties removed):
text1.split("\n");    // ["a", "b", "c"]      - 3 elements
text2.split("\n");    // ["a", "b"]           - 2 elements (empty removed!)
text3.split("\n");    // ["a", "b"]           - 2 elements (empties removed!)

// With -1 (trailing empties preserved):
text1.split("\n", -1); // ["a", "b", "c"]     - 3 elements
text2.split("\n", -1); // ["a", "b", ""]      - 3 elements (empty kept!)
text3.split("\n", -1); // ["a", "b", "", ""]  - 4 elements (empties kept!)
```

### Why use `-1` here?
In your case, using `-1 `ensures accurate line counting, especially if the text ends with
newlines or has empty lines at the end. Without it, you might undercount the lines.
However, looking at the input format in your code, the text is constructed without a trailing newline:
```java
if (j < lines - 1) {
    text.append("\n");  // No newline after last line
}
```
So technically, for this specific input format, `split("\n")` would work the same as `split("\n", -1)`.
But using `-1` is a **safer, more robust approach** for general text processing!