/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */


package org.apache.isis.core.progmodel.facets.disable;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.apache.isis.core.metamodel.authentication.AuthenticationSession;
import org.apache.isis.core.metamodel.authentication.AuthenticationSessionUtils;
import org.apache.isis.core.metamodel.facets.FacetHolder;
import org.apache.isis.core.metamodel.java5.ImperativeFacet;
import org.apache.isis.core.metamodel.util.InvokeUtils;


public class DisableForSessionFacetViaMethod extends DisableForSessionFacetAbstract implements ImperativeFacet {

    private final Method method;

    public DisableForSessionFacetViaMethod(
    		final Method method, 
    		final FacetHolder holder) {
        super(holder);
        this.method = method;
    }

    /**
     * Returns a singleton list of the {@link Method} provided in the constructor. 
     */
    public List<Method> getMethods() {
    	return Collections.singletonList(method);
    }

	public boolean impliesResolve() {
		return true;
	}

	public boolean impliesObjectChanged() {
		return false;
	}

    /**
     * Will only check provided that a {@link AuthenticationSession} has been provided.
     */
    public String disabledReason(final AuthenticationSession session) {
        if (session == null) {
            return null;
        }
        final int len = method.getParameterTypes().length;
        final Object[] parameters = new Object[len];
        parameters[0] = AuthenticationSessionUtils.createUserMemento(session);
        // TODO: need to change to pick up as non-static rather than static
        return (String) InvokeUtils.invokeStatic(method, parameters);
    }

    @Override
    protected String toStringValues() {
        return "method=" + method;
    }

}
