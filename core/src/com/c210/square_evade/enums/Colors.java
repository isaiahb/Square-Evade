package com.c210.square_evade.enums;

import com.badlogic.gdx.graphics.Color;

/**
 * Created by MacBook on 2015-12-29.
 */
public enum Colors {
	TopRed(230,73,58),
	BottomRed(189,57,41),
	TopBlue(50,170,230),
	BottomBlue(41,126,181);

	public Color color;
	Colors(int r, int g, int b) {
		color = new Color(r/255f, g/255f, b/255f, 1);
		System.out.println(this + " " + color);
	}
	Colors(int r, int g, int b, float a) {
		color = new Color(r/255f, g/255f, b/255f, a);
	}

	public static Color GetObbyColor(int position) {
		if (position > 0) return TopRed.color;
		else return BottomRed.color;
	}
}
