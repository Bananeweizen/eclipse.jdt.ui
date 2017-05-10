/*******************************************************************************
 * Copyright (c) 2000, 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Davids <sdavids@gmx.de> - initial API and implementation
 *     Achim Demelt <a.demelt@exxcellent.de> - [junit] Separate UI from non-UI code - https://bugs.eclipse.org/bugs/show_bug.cgi?id=278844
 *******************************************************************************/
package org.eclipse.jdt.internal.junit;

import java.util.List;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

/**
 * Default preference value initialization for the
 * <code>org.eclipse.jdt.junit</code> plug-in.
 */
public class JunitPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences prefs= DefaultScope.INSTANCE.getNode(JUnitCorePlugin.CORE_PLUGIN_ID);

		prefs.putBoolean(JUnitPreferencesConstants.DO_FILTER_STACK, true);

		prefs.putBoolean(JUnitPreferencesConstants.SHOW_ON_ERROR_ONLY, false);
		prefs.putBoolean(JUnitPreferencesConstants.ENABLE_ASSERTIONS, JUnitPreferencesConstants.ENABLE_ASSERTIONS_DEFAULT);

		List<String> defaults= JUnitPreferencesConstants.createDefaultStackFiltersList();
		String[] filters= defaults.toArray(new String[defaults.size()]);
		String active= JUnitPreferencesConstants.serializeList(filters);
		prefs.put(JUnitPreferencesConstants.PREF_ACTIVE_FILTERS_LIST, active);
		prefs.put(JUnitPreferencesConstants.PREF_INACTIVE_FILTERS_LIST, ""); //$NON-NLS-1$
		prefs.putInt(JUnitPreferencesConstants.MAX_TEST_RUNS, 10);

		// see https://github.com/junit-team/junit/issues/570
		prefs.put(JUnitPreferencesConstants.JUNIT3_JAVADOC, "http://junit.sourceforge.net/junit3.8.1/javadoc/"); //$NON-NLS-1$
		prefs.put(JUnitPreferencesConstants.JUNIT4_JAVADOC, "http://junit.org/junit4/javadoc/latest/"); //$NON-NLS-1$
		prefs.put(JUnitPreferencesConstants.HAMCREST_CORE_JAVADOC, "http://hamcrest.org/JavaHamcrest/javadoc/1.3/"); //$NON-NLS-1$

		String junit5JavadocLocation= "http://junit.org/junit5/docs/current/api/"; //$NON-NLS-1$
		prefs.put(JUnitPreferencesConstants.JUNIT_JUPITER_API_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_JUPITER_ENGINE_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_JUPITER_MIGRATIONSUPPORT_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_JUPITER_PARAMS_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_PLATFORM_COMMONS_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_PLATFORM_ENGINE_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_PLATFORM_LAUNCHER_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_PLATFORM_RUNNER_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_PLATFORM_SUITE_API_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_VINTAGE_ENGINE_JAVADOC, junit5JavadocLocation);
		prefs.put(JUnitPreferencesConstants.JUNIT_OPENTEST4J_JAVADOC, junit5JavadocLocation);
	}
}
