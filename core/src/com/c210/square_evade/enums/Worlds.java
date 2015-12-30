package com.c210.square_evade.enums;

/**
 * Created by MacBook on 2015-12-27.
 */


public enum Worlds  {
	Top(0),
	Bottom(1),
	Both(Top, Bottom);

	int bitmask;
	public int getMask() { return bitmask;}

	Worlds (int mask) {
		bitmask = 1 << mask;
	}

	Worlds (Worlds... ws) {
		bitmask = 0;
		for (Worlds w : ws)
			bitmask |= w.getMask();
	}

}
// looks complex? yea mad this so i can come back later to see how to do bitmask stuff with enums
/*
	if ((mycharacter & Character.Caring) == Character.Caring)
    	Console.WriteLine("The man is caring");
*/