=======================
What is JavaSnoop?
=======================
JavaSnoop is a tool for testing (re: hacking) Java desktop applications or applets.

=======================
Why do you need another tool besides an IDE or decompiler to hack these those apps?
=======================
Normally, without access to the original source code, testing the security of a
Java client is unpredictable at best and unrealistic at worst. With access the
original source, you can run a simple Java program and attach a debugger to it
remotely, stepping through code and changing variables where needed. Doing the
same with an applet is a little bit more difficult.

Unfortunately, real-life scenarios don't offer you this option, anyway.
Compilation and decompilation of Java are not really as deterministic as you
might imagine. Therefore, you can't just decompile a Java application, run it
locally and attach a debugger to it. 

Next, you may try to just alter the communication channel between the client and
the server, which is where most of the interesting things happen anyway. This
works if the client uses HTTP with a configurable proxy. Otherwise, you're stuck
with generic network traffic altering mechanisms. These are not so great for
almost all cases, because the data is usually not plaintext. It's usually a
custom protocol, serialized objects, encrypted, or some combination of those.

=======================
What exactly does this change?
=======================
Imagine trying to test a web application without Fiddler, Burp or WebScarab. The
same things would still be possible, but they'd be much less cost-effective and
the barrier to entry would be fairly high.

That sad situation is where Java application testability was yesterday, before
JavaSnoop. By the way, though, it's not just client applications, or applets
that can make use of JavaSnoop. Ever want to run that expensive Java desktop
program without having a valid license? Whoops, that's possible now too.

=======================
How does it work?
=======================
I'll explain it at a high level first. We wrote a program that lets you
"intercept" method calls in a Java process. Any Java process. To do that, we
install stageloading "hooks" with the Java Instrumentation API and some bytecode
engineering.

The coolest part is you don't really need to know much about Java to use the
program. We made some interfaces to make things super easy. Don't get me wrong
- a Java expert will really get all the horsepower out of it, but your everyday
Python coder will manage and your everyday non-OO Perl coder will be kind of
lost. A smart person that understands in general how virtual machines and
programs work will be great. Your everyday .NET expert will probably feel right
at home.

For a more detailed explanation of our technique, see the whitepaper or the
source code. 

=======================
Whenever I try to attach to a process, it says "no providers installed". How do I fix this?
or...
I can't seem to attach/hook functions with this Applet/WebStart program. What gives?
=======================
There's a couple things that could be wrong here. It very likely has something
to do with how you're starting JavaSnoop.

ANSWER #1: You started up the target process before you started up JavaSnoop.
           JavaSnoop's startup scripts disable Java security so that the evil
           agent JavaSnoop uses to instrument methods won't be restricted. If
           you run your target program before you startup JavaSnoop, that means
           it was started with the original, restrictive security rules in
           place. So kill the target application, restart it, and try attaching
           again.

ANSWER #2: You ran the JavaSnoop.jar directly instead of the startup script. As
           mentioned before, JavaSnoop's startup scripts turn off security
           and find a suitable JDK, alleviating some work from the user. If you
           try to run it directly, you'll have to do the things the startup
           scripts manually, which won't be fun. Just use the startup scripts.

ANSWER #3: Your JAVA_HOME environment variable is pointing to a JRE, not a JDK.
           JavaSnoop needs a file called 'tools.jar' (classes.jar on Mac) to
           do its work. This file isn't distributed with JavaSnoop since it's
           OS/JDK dependent. So you need to have a JDK to use JavaSnoop and you
           need to make sure JAVA_HOME points to one.


=======================
How do the startup scripts "turn off security" so that I know how to turn it back on?
=======================
Applets and Java Web Start applications are configured to run by default in a
fairly strict sandbox. Obviously, hacking privileged internal classes and
tampering with private fields is not usually allowed. This means we have to
essentially turn the security "off". To do this, perform the following steps:

1. Go to the Java security directory for your OS. Here are some hints:

Windows Vista/7: C:\Users\<user>\AppData\LocalLow\Sun\Java\Deployment\security\java.policy
Linux/Mac OSX: ~/.java.policy

2. A file called "java.policy" should exist (except on Windows). If it does,
copy it to "java.policy.bak", otherwise, create a new one.

3. Modify the "java.policy" file according to your OS:

Windows: Specifying the AllPermission permission on Windows to a specific
"codeBase" doesn't work [1], so users of this OS must dangerously run JavaSnoop
while granting access to all Java programs:

grant {
  permissions java.security.AllPermission;
};

MacOSX/Linux: On Mac OSX and Linux, you should be able to put the location of
the target program in the rule itself in order to grant the privileges to only
your target program. To run JavaSnoop on any applet from example.org, insert the
following rule anywhere in the file:

grant codeBase "http://example.org/-" {
  permissions java.security.AllPermission;
};

Or, to run JavaSnoop on a locally running application:

grant codeBase "file:/home/jdoe/JavaProgram" {
  permissions java.security.AllPermission;
};

4. Save the file
5. Restart the target program (including the browser)

After that's done, start the program you want to attach to, and you shouldn't
have any security exceptions anymore. You may run into this problem in something
other than an applet or Java Web Start program, because a normal Java thick
client can run in a sandbox just like they do.

Remember, you should always restore your settings back to normal after you're
done with JavaSnoop. An example of the standard policy can be found here, and
within JAVA_HOME/lib/security/java.policy:

http://www.javaworld.com/javaworld/jw-08-1998/sandbox/java.policy.html

========================
How do I know if the errors I'm getting are security errors?
========================

A key indicator that you're running into a security restriction problem is the
presence of scary RuntimeException, AccessControlException or SecurityException
stack traces in your console. If your program doesn't automatically have a
console, you can go into your Java settings and make Java create one for every
process. This actually helps your assessment in many different ways, since
useful info always ends up in the console. The steps for Windows:

1. Open up Control Panel
2. Open up Java settings
3. Go to "Advanced"
4. Go to "Java console"
5. Enable "Show console"

Remember, you should always restore your Java security settings back to normal
after you're done with JavaSnoop. Allowing any applet on the Internet complete
access to your machine is not ideal. Alternatively, you could write your own
permission file to grant codebases from your target domain AllPermission, and
leave the rest of the Internet in its usual sandbox. Buyer beware!

========================
I get an error when I try to attach - something about 64-bit and 32-bit, what gives?
========================

On 64-bit operating systems you may run into a situation where JavaSnoop is
trying to attach to a process that isn't running in the same CPU mode. For
instance, trying to attach to a 32-bit Java process while JavaSnoop was started
with in 64-bit Java won't work. The reverse is also true.

To make sure you can attach properly, make sure JavaSnoop is running on the same
Java architecture as the target application. If they line up, attaching should
work perfectly.

It will usually be easier to change how JavaSnoop is started than changing
how applets start, but the settings for how Java starts for applets or Java Web
Start programs can be found in the Java Control Panel.

========================
How come the class I'm looking for isn't in the list of classes to hook?
========================

Java loads classes in a lazy fashion. A class won't be "loaded" by the JVM until
the JVM needs it. JavaSnoop can only hook methods from classes that have been
loaded already. This means that you may have to cause your target application to
load the class you want to hook before you attach to it with JavaSnoop.

Since version 1.0 final, there are also UI options for asking the target process
to load classes by name. You can also point it to a JAR and say, "Hey. Load all
those classes now."

=======================
Is it free? What's the license?
=======================

Of course it's free! The license is GPLv3, and I'll explain why. I want this to
stay a community effort, and free. I also want any future improvements to be
required  to be offered back to me. We use this tool internally, so it will stay
up to date - but I want to make sure it's as good as it possibly can be. 

If one day I'm not doing a good job of managing it, someone let me know and I'll
make it BSD.