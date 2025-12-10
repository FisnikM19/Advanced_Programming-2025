### 1) The `Collectors.groupingBy()` method has a default behavior built into it.

When you use:

`
Collectors.groupingBy(Course::getDifficulty)
`

It's actually a shorthand for:

`
Collectors.groupingBy(Course::getDifficulty, Collectors.toList())
`

How it works:
- `groupingBy` takes a classifier function (in your case, `Course::getDifficulty`)
- By default, it uses a **downstream collector** of `Collectors.toList()` to accumulate the values
- So for each difficulty key, it automatically collects all courses with that difficulty into a `List<Course>`

You can customize this if needed:
```java
// Default - collects to List
Collectors.groupingBy(Course::getDifficulty)

// Collect to Set instead
Collectors.groupingBy(Course::getDifficulty, Collectors.toSet())

// Count how many courses per difficulty
Collectors.groupingBy(Course::getDifficulty, Collectors.counting())

// Use a TreeMap instead of HashMap
Collectors.groupingBy(Course::getDifficulty, TreeMap::new, Collectors.toList())
```

So the `List<Course>` return type comes from the default downstream collector `(toList())` that `groupingBy` uses when you don't specify one explicitly.

### 2) The `sorted()` method without arguments (when we have `List<String>` object):
- `sorted()` without arguments uses the natural ordering of strings (alphabetical)

### 3) Here's how to use `IntSummaryStatistics` with streams:
```java
public IntSummaryStatistics getEnrollmentStatistics() {
    return departments.stream()
            .flatMap(department -> department.getCourses().stream())
            .collect(Collectors.summarizingInt(Course::getEnrolledStudents));
}
```
**How it works:**
1. `flatMap` - Flatten all courses from all departments into one stream.
2. `.collect(Collectors.summarizingInt(Course::getEnrolledStudents))` - Automatically creates an `IntSummaryStatistics` object with all the statistics.
3. What `IntSummaryStatistics` contains:
   - count - Number of courses
   - sum - Total enrolled students across all courses
   - min - Minimum enrollment in any course
   - max - Maximum enrollment in any course
   - average - Average enrollment per course

