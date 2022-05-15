package zomlink;

import astra.event.Event;
import astra.reasoner.util.LogicVisitor;
import astra.term.ListTerm;
import astra.term.Term;

public class ZombieEvent implements Event {
    Term type;
    ListTerm args;

    public ZombieEvent(Term type) {
        this.type = type;
        args = new ListTerm();
    }

    public ZombieEvent(Term id, ListTerm args) {
        this.type = id;
        this.args = args;
    }

    public Object getSource() {
        return null;
    }

    public String signature() {
        return "$zombie:";
    }

    public Event accept(LogicVisitor visitor) {
        return new ZombieEvent((Term) type.accept(visitor), (ListTerm) args.accept(visitor));
    }
}

