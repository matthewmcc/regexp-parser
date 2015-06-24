Overview:     Implement a regexp pattern searching program using the FSM, deque and compiler techniques outlined in class. Your program must accept a regexp pattern as a command-line argument (enclosed within double-quotes—see "Note" below), and produce as output each line of the input that contains a match for the specified pattern. Input is obtained from a file whose name is given as a second command-line argument. Any line of input that matches the regexp is output just once, regardless of how many subsequent times the pattern might be satisfied by that line.

Regexp specification:     For this assignment, a wellformed regexp is specified as follows:

any symbol that does not have a special meaning (as given below) is a regexp that matches itself—that is, it is a literal symbol (i.e. terminal)
* indicates closure (zero or more occurrences) on the preceding regexp
? indicates that the preceding regexp can occur zero or one time
. is a wildcard symbol that matches any literal
consecutive, adjacent regexps are concatenated to form a single regexp
| is an infix alternation operator such that if r and e are regexps, then r|e is a regexp that matches one of either r or e
[ and ] may enclose a list of literals to form a regular expression that matches one and only one literal in that list. Any special characters in that list lose their special meaning and become literals; but there is a special case—if ] is to be one of those literals then it must appear first in the list. An empty list is not allowed.
( and ) may enclose a regexp to raise its precedence in the usual manner; such that if e is a regexp, then (e) is a regexp and is equivalent to e. e cannot be empty.
\ is an escape character that matches nothing but indicates the symbol immediately following the backslash loses any special meaning and is to be interpretted as a literal symbol (e.g. "\." matches a single period, "\\" matches a single backslash, and "\a" matches a single 'a')
operator precedence is as follows (from high to low):
escaped characters (i.e. symbols preceded by \)
parentheses (i.e. the most deeply nested regexps have the highest precedence)
closure and option operators (i.e. * and ?)
concatenation
alternation (i.e. |, [ and ])
Note:     Operating system shells typically parse command-line arguments as regular expressions, and some of the special characters defined for this assignment are also special characters for the command-line interpreter of various operating systems. This can make it hard to pass your regexp into the argument vector of your program. You can get around most problems by simply enclosing your regexp command-line argument within double-quote characters, which is what you should do for this assignment. To get a double-quote character into your regexp, you have to escape it by putting a backslash in front of it, and then the backslash is removed by the time the string gets into your program's command-line argument vector. There is only one other situation where Linux shells remove a backslash character from a quoted string, and that is when it precedes another backslash. For this assignment, it is the string that gets into your program that is the regexp—which may entail some extra backslashes in the argument. (N.b. Windows command prompt shell has a different syntax for parsing regexps than does Linux, so if you develop on a windows box, make sure you make the necessary adjustments for it to run under linux.)
Approach:     One way to split up the assignment is to have one partner start implementing the pattern searcher from a hand-crafted FSM stored in a file, while the other partner writes the parser. Once the parser is working well, turn it into a compiler and have it write the FSM to a file that the searcher can load. I have more or less given you the (pseudo) code for most of this assignment, but make sure you draft a good grammar to cover all the new bits before you start hacking your parser!

You must implement your own parser/compiler, and your own FSM (simulating two-state and branching machines) similar to how it was shown to you in class, and you must implement your own dequeue to support the search (i.e. no Java libraries or other tools for these).