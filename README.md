# simple-search


### Dependencies 

Make sure you have `sbt` and `scala` installed. I am using scala `2.12.x` and sbt `1.2.x`
#### How to build

- Go to project root and write 
```
$ sbt
```
It'll start the sbt shell. 

- Now run the application with this command
```
> runMain test.SimpleSearch /path/to/directory/containing/text/files
```
It'll build the index with all the files and a prompt will appear where you can search for words like:
```
search> hello world
```
And the result will look like:
```
file1.txt : 100%
file2.txt : 50%
```
To terminate the search cli type this
```
search> :quit
```

___
 
### Few declarations about current domain/context  

- What constitutes a word?
  - A word is continuous string without space. 
- What constitutes two words being equal?
  - Two words are equal if they have same alphabets in same order. Note that we are ignoring case here, which means both "this" and "This" are will be treated as equal. 
- How are we indexing?
  - Currently the parser/tokenizer is very basic which parse the string and create word(tokens/terms), no space and special character is considered. All the words are converted to lower case before query or index generation.
- Any limitations or missing things?
  - Right now the code is very basic, all the files are processed sequentially. Ideally we can do index generation task for files in parallel to make max usage of resources. But yes, it has a cost like complicated code, race conditions etc.
  - The word search algorithm very basic, it just checks word is present in file or not and not handling lot of use cases.
  - We don't have any blacklisting of words, currently we are indexing everything.
- Important thing
  - I keep writing (saying while I talk) the word "we" instead of "I" even in single contributor project because its about team, team of you(reader) and me. Just joking :) have fun. I know its strange :|  
