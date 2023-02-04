package com.beanloaf.res.theme;

import com.formdev.flatlaf.FlatDarculaLaf;

public class ThoughtsTheme
	extends FlatDarculaLaf
{
	public static final String NAME = "thoughtTheme";

	public static boolean setup() {
		return setup( new ThoughtsTheme() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, ThoughtsTheme.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
