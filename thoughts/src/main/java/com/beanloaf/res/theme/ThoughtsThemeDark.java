package com.beanloaf.res.theme;

import com.formdev.flatlaf.FlatDarculaLaf;

public class ThoughtsThemeDark
	extends FlatDarculaLaf
{
	public static final String NAME = "ThoughtsTheme";

	public static boolean setup() {
		return setup( new ThoughtsThemeDark() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, ThoughtsThemeDark.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
