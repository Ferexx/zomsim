package zomlink;

import java.util.HashMap;
import java.util.Map;

import astra.core.Agent;
import astra.reasoner.EventUnifier;
import astra.reasoner.Unifier;
import astra.term.Term;

public class ZombieEventUnifier implements EventUnifier<ZombieEvent> {

    @Override
    public Map<Integer, Term> unify(ZombieEvent source, ZombieEvent target, Agent agent) {
        return Unifier.unify(
                new Term[] {source.type, source.args},
                new Term[] {target.type, target.args},
                new HashMap<Integer, Term>(),
                agent);
    }
}

