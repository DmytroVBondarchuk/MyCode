/**
 * Eliza
 * Author: Charles Hayden
 * http://www.chayden.net/eliza/Eliza.html
 * Modified by Andres Colubri to use it as a Processing library
 * Modified by Andrew Murray for the purposes of encapsulation
 */

package codeanticode.eliza;


/**
 *  Eliza main class.
 *  Stores the processed script.
 *  Does the input transformations.
 */
public class Eliza {
    private final boolean echoInput = false;
    private final boolean printData = false;

    private final boolean printKeys = false;
    private final boolean printSyns = false;
    private final boolean printPrePost = false;
    private final boolean printInitialFinal = false;

    private KeyList keys = new KeyList();
    private SynList syns = new SynList();
    private PrePostList pre = new PrePostList();
    private PrePostList post = new PrePostList();
    private String initial = "Hello.";
    private String finl = "Goodbye.";
    private WordList quit = new WordList();

    private KeyStack keyStack = new KeyStack();

    private Mem mem = new Mem();

    private DecompList lastDecomp;
    private ReasembList lastReasemb;
    private boolean finished = false;

    private static final int success = 0;
    private static final int failure = 1;
    private static final int gotoRule = 2;


    public Eliza() {
        readDefaultScript();
    }


    /**
     *  Process a line of script input.
     */
    private void collect(String s) {
        String lines[] = new String[4];

        if (EString.match(s, "*reasmb: *", lines)) {
            if (lastReasemb == null) {
                System.out.println("Error: no last reasemb");
                return;
            }
            lastReasemb.add(lines[1]);
        } else if (EString.match(s, "*decomp: *", lines)) {
            if (lastDecomp == null) {
                System.out.println("Error: no last decomp");
                return;
            }
            lastReasemb = new ReasembList();
            String temp = new String(lines[1]);
            if (EString.match(temp, "$ *", lines)) {
                lastDecomp.add(lines[0], true, lastReasemb);
            } else {
                lastDecomp.add(temp, false, lastReasemb);
            }
        } else if (EString.match(s, "*key: * #*", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            int n = 0;
            if (lines[2].length() != 0) {
                try {
                    n = Integer.parseInt(lines[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Number is wrong in key: " + lines[2]);
                }
            }
            keys.add(lines[1], n, lastDecomp);
        } else if (EString.match(s, "*key: *", lines)) {
            lastDecomp = new DecompList();
            lastReasemb = null;
            keys.add(lines[1], 0, lastDecomp);
        } else if (EString.match(s, "*synon: * *", lines)) {
            WordList words = new WordList();
            words.add(lines[1]);
            s = lines[2];
            while (EString.match(s, "* *", lines)) {
                words.add(lines[0]);
                s = lines[1];
            }
            words.add(s);
            syns.add(words);
        } else if (EString.match(s, "*pre: * *", lines)) {
            pre.add(lines[1], lines[2]);
        } else if (EString.match(s, "*post: * *", lines)) {
            post.add(lines[1], lines[2]);
        } else if (EString.match(s, "*initial: *", lines)) {
            initial = lines[1];
        } else if (EString.match(s, "*final: *", lines)) {
            finl = lines[1];
        } else if (EString.match(s, "*quit: *", lines)) {
            quit.add(" " + lines[1] + " ");
        } else {
            System.out.println("Unrecognized input: " + s);
        }
    }

    /**
     *  Print the stored script.
     */
    public void printScript() {
        if (printKeys) keys.print(0);
        if (printSyns) syns.print(0);
        if (printPrePost) {
            pre.print(0);
            post.print(0);
        }
        if (printInitialFinal) {
            System.out.println("initial: " + initial);
            System.out.println("final:   " + finl);
            quit.print(0);
            quit.print(0);
        }
    }

    /**
     * Gets Eliza's initial greeting
     * @return Eliza's initial greeting
     */
    public String getGreeting() {
        return initial;
    }

    /**
     * Returns whether Eliza has finished a conversation. Typically, this happens after
     * a user says "Goodbye"
     * @return true if eliza is finished, false otherwise.
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * Process a line of user input and generate a response
     * @param userInput input from the user
     * @return a reply to the user input
     */
    public String processInput(String userInput) {
        String reply;
        //  Do some input transformations first.
        userInput = EString.translate(userInput, "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
                "abcdefghijklmnopqrstuvwxyz");
        userInput = EString.translate(userInput, "@#$%^&*()_-+=~`{[}]|:;<>\\\"",
                "                          ");
        userInput = EString.translate(userInput, ",?!", "...");
        //  Compress out multiple speace.
        userInput = EString.compress(userInput);
        String lines[] = new String[2];
        //  Break apart sentences, and do each separately.
        while (EString.match(userInput, "*.*", lines)) {
            reply = sentence(lines[0]);
            if (reply != null) return reply;
            userInput = EString.trim(lines[1]);
        }
        if (userInput.length() != 0) {
            reply = sentence(userInput);
            if (reply != null) return reply;
        }
        //  Nothing matched, so try memory.
        String m = mem.get();
        if (m != null) return m;

        //  No memory, reply with xnone.
        Key key = keys.getKey("xnone");
        if (key != null) {
            Key dummy = null;
            reply = decompose(key, userInput, dummy);
            if (reply != null) return reply;
        }
        //  No xnone, just say anything.
        return "I am at a loss for words.";
    }

    private boolean readDefaultScript() {
        clearScript();

        String[] lines = Constant.DEFAULT_SCRIPT.split("\n");
        if (lines == null || lines.length == 0) {
            System.err.println("Cannot load Eliza script!");
            return false;
        } else {
            for (int i = 0; i < lines.length; i++) {
                collect(lines[i]);
            }
            return true;
        }
    }
    private void clearScript() {
        keys.clear();
        syns.clear();
        pre.clear();
        post.clear();
        initial = "";
        finl = "";
        quit.clear();
        keyStack.reset();
    }

    /**
     *  Process a sentence.
     *  (1) Make pre transformations.
     *  (2) Check for quit word.
     *  (3) Scan sentence for keys, build key stack.
     *  (4) Try decompositions for each key.
     */
    private String sentence(String s) {
        s = pre.translate(s);
        s = EString.pad(s);
        if (quit.find(s)) {
            finished = true;
            return finl;
        }
        keys.buildKeyStack(keyStack, s);
        for (int i = 0; i < keyStack.keyTop(); i++) {
            Key gotoKey = new Key();
            String reply = decompose(keyStack.key(i), s, gotoKey);
            if (reply != null) return reply;
            //  If decomposition returned gotoKey, try it
            while (gotoKey.key() != null) {
                reply = decompose(gotoKey, s, gotoKey);
                if (reply != null) return reply;
            }
        }
        return null;
    }

    /**
     *  Decompose a string according to the given key.
     *  Try each decomposition rule in order.
     *  If it matches, assemble a reply and return it.
     *  If assembly fails, try another decomposition rule.
     *  If assembly is a goto rule, return null and give the key.
     *  If assembly succeeds, return the reply;
     */
    private String decompose(Key key, String s, Key gotoKey) {
        String reply[] = new String[10];
        for (int i = 0; i < key.decomp().size(); i++) {
            Decomp d = (Decomp) key.decomp().elementAt(i);
            String pat = d.pattern();
            if (syns.matchDecomp(s, pat, reply)) {
                String rep = assemble(d, reply, gotoKey);
                if (rep != null) return rep;
                if (gotoKey.key() != null) return null;
            }
        }
        return null;
    }

    /**
     *  Assembly a reply from a decomp rule and the input.
     *  If the reassembly rule is goto, return null and give
     *    the gotoKey to use.
     *  Otherwise return the response.
     */
    private String assemble(Decomp d, String reply[], Key gotoKey) {
        String lines[] = new String[3];
        d.stepRule();
        String rule = d.nextRule();
        if (EString.match(rule, "goto *", lines)) {
            //  goto rule -- set gotoKey and return false.
            gotoKey.copy(keys.getKey(lines[0]));
            if (gotoKey.key() != null) return null;
            System.out.println("Goto rule did not match key: " + lines[0]);
            return null;
        }
        String work = "";
        while (EString.match(rule, "* (#)*", lines)) {
            //  reassembly rule with number substitution
            rule = lines[2];        // there might be more
            int n = 0;
            try {
                n = Integer.parseInt(lines[1]) - 1;
            } catch (NumberFormatException e) {
                System.out.println("Number is wrong in reassembly rule " + lines[1]);
            }
            if (n < 0 || n >= reply.length) {
                System.out.println("Substitution number is bad " + lines[1]);
                return null;
            }
            reply[n] = post.translate(reply[n]);
            work += lines[0] + " " + reply[n];
        }
        work += rule;
        if (d.mem()) {
            mem.save(work);
            return null;
        }
        return work;
    }
}
