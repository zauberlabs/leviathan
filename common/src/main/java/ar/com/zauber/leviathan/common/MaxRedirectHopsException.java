/*
 * Copyright (c) 2011 Zauber S.A.  -- All rights reserved
 */
package ar.com.zauber.leviathan.common;

/**
 * An exception that is thrown when the maximum amount of redirect
 * hops has been reached.
 * 
 * @author Guido Marucci Blas
 * @since Feb 9, 2011
 */
public final class MaxRedirectHopsException extends RuntimeException {
    
    /** <code>serialVersionUID</code> */
    private static final long serialVersionUID = 760156010553433686L;
    
    private final int hops;
    private final int maxHops;
    
    /**
     * Creates the MaxRedirectHopsException.
     *
     * @param hops
     */
    public MaxRedirectHopsException(final int hops, final int maxHops) {
        this.hops = hops;
        this.maxHops = maxHops;
    }

    /**
     * Returns the hops.
     * 
     * @return <code>int</code> with the hops.
     */
    public int getHops() {
        return hops;
    }

    /**
     * Returns the maxHops.
     * 
     * @return <code>int</code> with the maxHops.
     */
    public int getMaxHops() {
        return maxHops;
    }
    
}
