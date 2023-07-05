package games.chinesecheckers;

import core.AbstractParameters;
import gametemplate.GTParameters;

public class CCParameters extends AbstractParameters {
    public CCParameters(long seed) {
        super(seed);
    }


    @Override
    protected AbstractParameters _copy() {
        // TODO: deep copy of all variables.
        return this;
    }

    @Override
    protected boolean _equals(Object o) {
        // TODO: compare all variables.
        return o instanceof GTParameters;
    }

    @Override
    public int hashCode() {
        // TODO: include the hashcode of all variables.
        return super.hashCode();
    }
}
