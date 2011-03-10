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


package org.apache.isis.runtimes.dflt.runtime.authorization.standard;

import org.apache.isis.applib.Identifier;
import org.apache.isis.core.commons.authentication.AuthenticationSession;
import org.apache.isis.core.commons.config.IsisConfiguration;
import org.apache.isis.core.metamodel.adapter.ObjectAdapter;
import org.apache.isis.runtimes.dflt.runtime.authorization.AuthorizationManagerAbstract;

public class AuthorizationManagerStandard extends AuthorizationManagerAbstract {

    private Authorizor authorizor;

    ///////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////
    
	public AuthorizationManagerStandard(final IsisConfiguration configuration) {
    	super(configuration);
    	// avoid null pointers
    	authorizor = new Authorizor(){

			@Override
			public void init() {
			}

			@Override
			public void shutdown() {
			}

			@Override
			public boolean isVisibleInRole(String user, Identifier identifier) {
				return true;
			}

			@Override
			public boolean isUsableInRole(String role, Identifier identifier) {
				return true;
			}}; 
    }

    ///////////////////////////////////////////////////////////
    // init, shutddown
    ///////////////////////////////////////////////////////////
	
    public void init() {
        authorizor.init();
    }

    public void shutdown() {
        authorizor.shutdown();
    }
	
	
    ///////////////////////////////////////////////////////////
    // API
    ///////////////////////////////////////////////////////////

    public boolean isUsable(final AuthenticationSession session, ObjectAdapter target, final Identifier identifier) {
        if (isPerspectiveMember(identifier)) {
            return true;
        }
        for (String roleName: session.getRoles()) {
            if (authorizor.isUsableInRole(roleName, identifier)) {
                return true;
            }
        }
        return false;
    }

    public boolean isVisible(final AuthenticationSession session, ObjectAdapter target, final Identifier identifier) {
        if (isPerspectiveMember(identifier)) {
            return true;
        }
        for (String roleName: session.getRoles()) {
            if (authorizor.isVisibleInRole(roleName, identifier)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPerspectiveMember(final Identifier identifier) {
        return (identifier.getClassName().equals(""));
    }
    
    
    ////////////////////////////////////////////////////
    // Dependencies (injected) 
    ////////////////////////////////////////////////////
    
    protected void setAuthorizor(final Authorizor authorisor) {
        this.authorizor = authorisor;
    }
}