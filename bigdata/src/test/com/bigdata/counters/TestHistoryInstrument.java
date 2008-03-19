/*

Copyright (C) SYSTAP, LLC 2006-2008.  All rights reserved.

Contact:
     SYSTAP, LLC
     4501 Tower Road
     Greensboro, NC 27410
     licenses@bigdata.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/
/*
 * Created on Mar 19, 2008
 */

package com.bigdata.counters;

import junit.framework.TestCase2;


/**
 * @author <a href="mailto:thompsonbry@users.sourceforge.net">Bryan Thompson</a>
 * @version $Id$
 */
public class TestHistoryInstrument extends TestCase2 {
    
    // 60 seconds.
    final long t60 = 60*1000;
    
    // time zero.
    final long t0 = System.currentTimeMillis();
    
    /**
     * 
     */
    public TestHistoryInstrument() {
        super();
    }

    /**
     * @param arg0
     */
    public TestHistoryInstrument(String arg0) {
        super(arg0);
    }

    /**
     * Test of {@link History} adds two samples spaced one minute apart and then
     * a 3rd sample that is two minutes later.
     */
    public void test_history01() {
    
        // a history buffer with 60 samples each spaced 60 seconds apart.
        final History<Double> h = new History<Double>(new Double[60], t60);

        assertEquals(0,h.size());
        assertEquals(60,h.capacity());

        log.info("\n"+h.toString());
        
        // add the first sample.
        h.add(t0,12d);

        assertEquals(1,h.size());
        assertEquals(60,h.capacity());
        assertEquals(12d,h.getAverage().doubleValue());

        log.info("\n"+h.toString());

        // add a 2nd sample.
        h.add(t0+t60,6d);

        assertEquals(2,h.size());
        assertEquals(60,h.capacity());
        assertEquals(((6d+12d)/2d),h.getAverage().doubleValue());

        log.info("\n"+h.toString());

        // add a 2nd sample, but skip 60 seconds.
        h.add(t0+t60*3,9d);

        assertEquals(3,h.size());
        assertEquals(60,h.capacity());
        assertEquals(((6d+12d+9d)/3d),h.getAverage().doubleValue());

        log.info("\n"+h.toString());

    }
    
    /**
     * Test that overflow occurs correctly using a short buffer.
     */
    public void test_historyOverflow() {
        
        /*
         * a history buffer with 2 samples each spaced 60 seconds apart.
         * 
         */
        final History<Double> h = new History<Double>(new Double[2], t60);

        assertEquals(0,h.size());
        assertEquals(2,h.capacity());

        log.info("\n"+h.toString());
        
        /*
         * a history buffer with 2 samples each spaced two minutes apart.
         * 
         */
        final History<Double> h2 = new History<Double>(3,h);

        assertEquals(0,h2.size());
        assertEquals(3,h2.capacity());

        log.info("\n"+h2.toString());

        /*
         * feed in data.
         */

        // add the first sample.
        h.add(t0,12d);

        assertEquals(1,h.size());
        
        assertEquals(12d,h.getAverage().doubleValue());

        log.info("\n"+h.toString());

        // add a 2nd sample.
        h.add(t0+t60,6d);

        assertEquals(2,h.size());
        
        assertEquals(((6d+12d)/2d),h.getAverage().doubleValue());

        log.info("\n"+h.toString());
        
        /*
         * add a 3rd sample, this should cause the first buffer to overflow.
         */
        h.add(t0+t60+t60,9d);

        assertEquals(2,h.size());
        
        assertEquals(((6d+9d)/2d),h.getAverage().doubleValue());

        log.info("\n"+h.toString());

        assertEquals(1,h2.size());
        
        // overflow should propagate the average before adding the new sample.
        assertEquals((12d+6d)/2d,h2.getAverage().doubleValue());
        
        log.info("\n"+h2.toString());

    }
    
    /**
     * Test {@link HistoryInstrument}.
     */
    public void test_001() {
        
        HistoryInstrument<Double> h = new HistoryInstrument<Double>(new Double[]{});

        assertEquals(60,h.minutes.capacity());
        assertEquals(24,h.hours.capacity());
        assertEquals(30,h.days.capacity());

        assertTrue(h.minutes.isNumeric());
        assertFalse(h.minutes.isLong());
        assertTrue(h.minutes.isDouble());

        log.info(h.toString());

        /*
         * Fill the entire buffer with per-minute samples and verify that we
         * overflow to the per hour samples buffer only on the last minute.
         */
        for(int i=0; i<60; i++) {
            
            h.minutes.add(t0+i*t60, (double)i);
            
            assertEquals(i+1,h.minutes.size());

            if(i+1==60) {

                assertEquals(1,h.hours.size());
                
            } else {
                
                assertEquals(0,h.hours.size());
                
            }
            
        }
        
        log.info(h.toString());
        
    }
    
}
