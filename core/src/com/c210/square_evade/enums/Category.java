package com.c210.square_evade.enums;

/**
 * Created by MacBook on 2015-12-27.
 */
public enum Category {
	TopPlayer(0),
	BottomPlayer(1),
	Obstacle(2),
	Ground(3),
	Nothing(4);

	public short bit;
	Category(int mask) {
		bit = (short) (1<<mask);
		System.out.println(this);
		System.out.println(bit);
	}
}
