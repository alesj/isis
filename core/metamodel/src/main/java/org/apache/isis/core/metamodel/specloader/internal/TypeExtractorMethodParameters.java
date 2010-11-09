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


package org.apache.isis.core.metamodel.specloader.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Helper that finds all parameter types (including generic types) for the
 * provided {@link Method}.
 * 
 * <p>
 * For example,
 * <pre>
 * public class CustomerRepository {
 *     public void filterCustomers(List<Customer> customerList) { ... }
 * }
 * </pre>
 * <p>
 * will find both <tt>List</tt> and <tt>Customer</tt>. 
 */
public class TypeExtractorMethodParameters extends TypeExtractorAbstract {
    
    private Class<?>[] parameterTypes;

    public TypeExtractorMethodParameters(final Method method) {
        super(method);
        
        parameterTypes = getMethod().getParameterTypes();
        for(Class<?> parameterType: parameterTypes) {
            add(parameterType);
        }
        
        Type[] genericTypes = getMethod().getGenericParameterTypes();
        for(Type genericType: genericTypes) {
            addParameterizedTypes(genericTypes);
        }
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }
    
}
