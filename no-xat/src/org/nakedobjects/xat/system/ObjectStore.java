package org.nakedobjects.xat.system;

import org.nakedobjects.object.LoadedObjects;
import org.nakedobjects.object.Naked;
import org.nakedobjects.object.NakedClass;
import org.nakedobjects.object.NakedObject;
import org.nakedobjects.object.NakedObjectManager;
import org.nakedobjects.object.NakedObjectStore;
import org.nakedobjects.object.NakedValue;
import org.nakedobjects.object.ObjectNotFoundException;
import org.nakedobjects.object.ObjectStoreException;
import org.nakedobjects.object.UnsupportedFindException;
import org.nakedobjects.object.reflect.Field;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.log4j.Category;


public class ObjectStore implements NakedObjectStore {
    private final static Category LOG = Category.getInstance(ObjectStore.class);
    private final Hashtable classes;
    private final LoadedObjects loaded;
    /*
     * The objects need to store in a repeatable sequence so the elements and instances method
     * return the same data for any repeated call, and so that one subset of instances follows on
     * the previous. This is done by keeping the objects in the order that they where created.
     */
 //   private final Vector persistentObjectVector;
    	private final Hashtable objectInstances;

    public ObjectStore() {
        loaded = new LoadedObjects();
//        persistentObjectVector = new Vector();
        objectInstances = new Hashtable();
        classes = new Hashtable(30);
    }

    public void abortTransaction() {}

    public void createObject(NakedObject object) throws ObjectStoreException {
        LOG.debug("createObject " + object);
        save(object);
    }

    public void createNakedClass(NakedClass cls) throws ObjectStoreException {
        LOG.debug("createClass " + cls);
        cls.setResolved();
        classes.put(cls.fullName(), cls);
        Hashtable persistentObjectVector = instancesFor(cls);
        persistentObjectVector.put(cls.getOid(), cls);
    }
    
    private Hashtable instancesFor(NakedClass cls) {
        if(objectInstances.containsKey(cls)) {
            return (Hashtable) objectInstances.get(cls);
        } else {
            Hashtable instances = new Hashtable();
            objectInstances.put(cls, instances);
            return instances;
        }
    }

    public void destroyObject(NakedObject object) throws ObjectStoreException {
        LOG.info("Delete requested on '" + object + "'");
        Hashtable persistentObjectVector = instancesFor(object.getNakedClass());
        persistentObjectVector.remove(object.getOid());
    }

    private Enumeration elements(NakedClass nakedClass) {
        Hashtable persistentObjectVector = instancesFor(nakedClass);
        return persistentObjectVector.elements();
    }

    public void endTransaction() {
        LOG.debug("end transaction");
    }

    public String getDebugData() {
        return ""; //persistentObjectVector.toString();
    }

    public String getDebugTitle() {
        return name();
    }

    public Vector getInstances(NakedClass cls, boolean includeSubclasses) {
        Vector instances = new Vector();
        Enumeration objects = elements(cls);

        while (objects.hasMoreElements()) {
            NakedObject object = (NakedObject) objects.nextElement();

            if (cls.equals(object.getNakedClass())) {
                instances.addElement(object);
            }
        }

        LOG.debug("getInstances of " + cls + " => " + instances);
        return instances;
    }

	public Vector getInstances(NakedClass cls, String pattern, boolean includeSubclasses) throws ObjectStoreException, UnsupportedFindException {
	   	Vector v = getInstances(cls, false);
	   	int i = 0;
	   	String match = pattern.toLowerCase();
	   	while(i < v.size()) {
	   	    NakedObject element = (NakedObject) v.elementAt(i);
            if(element.title().toString().toLowerCase().indexOf(match) == -1) {
                v.removeElementAt(i);
            } else {
                i++;
            }
	   	}
	   	return v;
	}
    
	public Vector getInstances(NakedObject pattern, boolean includeSubclasses) {
        if (pattern == null) { throw new NullPointerException(); }

        Vector instances = new Vector();
        if (pattern instanceof NakedClass) {
            //            NakedClass requiredType = pattern.getNakedClass();
            Enumeration objects = elements((NakedClass) pattern);

            String name = ((NakedClass) pattern).fullName();

            while (objects.hasMoreElements()) {
                NakedObject object = (NakedObject) objects.nextElement();

                if (object instanceof NakedClass && ((NakedClass) object).getName().isSameAs(name)) {
                    instances.addElement(object);
                }
            }
        } else {
            NakedClass requiredType = pattern.getNakedClass();
            Enumeration objects = elements(requiredType);

            while (objects.hasMoreElements()) {
                NakedObject object = (NakedObject) objects.nextElement();

                if (requiredType.equals(object.getNakedClass()) && matchesPattern(pattern, object)) {
                    instances.addElement(object);
                }
            }
        }
        LOG.debug("getInstances like " + pattern + " => " + instances);
        return instances;
    }

    public LoadedObjects getLoadedObjects() {
        return loaded;
    }

    public NakedObject getObject(Object oid, NakedClass hint) throws ObjectNotFoundException, ObjectStoreException {
        LOG.debug("getObject " + oid);
        Enumeration e = elements(hint);
        while (e.hasMoreElements()) {
            NakedObject instance = (NakedObject) e.nextElement();
            if (instance.getOid().equals(oid)) {
                LOG.debug("  got " + instance);
                return instance;
            }
        }
        throw new ObjectNotFoundException(oid);
    }

    public NakedClass getNakedClass(String name) throws ObjectNotFoundException, ObjectStoreException {
        NakedClass nc = (NakedClass) classes.get(name);
        if(nc == null) {
            throw new ObjectNotFoundException();
        } else {
            return nc;
        }
    }
    
    public boolean hasInstances(NakedClass cls, boolean includeSubclasses) {
        Enumeration e = elements(cls);

        while (e.hasMoreElements()) {
            NakedObject data = (NakedObject) e.nextElement();
            if (cls.equals(data.getNakedClass())) {
                LOG.debug("hasInstances of " + cls);
                return true;
            }
        }
        LOG.debug("Failed - hasInstances of " + cls);
        return false;
    }

    public void init() throws ObjectStoreException {}

    private boolean matchesPattern(NakedObject pattern, NakedObject instance) {
        NakedObject object = instance;
        NakedClass nc = object.getNakedClass();
        Field[] fields = nc.getFields();

        for (int f = 0; f < fields.length; f++) {
            Field fld = fields[f];

            // are ignoring internal collections - these probably should be considered
            // ignore derived fields - there is no way to set up these fields
            if (fld.isPart() || fld.isDerived()) {
                continue;
            }

            if (fld.isValue()) {
                // find the objects
                NakedValue reqd = (NakedValue) fld.get(pattern);
                NakedValue search = (NakedValue) fld.get(object);

                // if pattern contains empty value then it matches anything
                if (reqd.isEmpty()) {
                    continue;
                }

                // compare the titles
                String r = reqd.title().toString().toLowerCase();
                String s = search.title().toString().toLowerCase();

                // if the pattern occurs in the object
                if (s.indexOf(r) == -1) { return false; }
            } else {
                // find the objects
                Naked reqd = fld.get(pattern);
                Naked search = fld.get(object);

                // if pattern contains null reference then it matches anything
                if (reqd == null) {
                    continue;
                }

                // otherwise there must be a reference, else they can never match
                if (search == null) { return false; }

                if (reqd != search) { return false; }
            }
        }

        return true;
    }

    public String name() {
        return "Transient Object Store";
    }

    public int numberOfInstances(NakedClass cls, boolean includedSubclasses) {
        int noInstances = 0;
        Enumeration e = elements(cls);

        while (e.hasMoreElements()) {
            NakedObject data = (NakedObject) e.nextElement();
            if (cls.equals(data.getNakedClass())) {
                noInstances++;
            }
        }

        LOG.debug("numberOfInstances of " + cls + " = " + noInstances);
        return noInstances;
    }

    public void resolve(NakedObject object) throws ObjectStoreException {
        LOG.debug("resolve " + object);

        NakedObject o = getObject(object.getOid(), null);
        object.copyObject(o);
    }

    public void save(NakedObject object) throws ObjectStoreException {
        LOG.debug("save " + object);
        if (object instanceof NakedClass) { 
            throw new ObjectStoreException("Can't make changes to a NakedClass object"); 
        }
        Hashtable persistentObjectVector = instancesFor(object.getNakedClass());
        Object oid = object.getOid();
  //      if (!persistentObjectVector.containsKey(oid)) {
            persistentObjectVector.put(oid, object);
   //     }
    }

    public void setObjectManager(NakedObjectManager manager) {}

    public void shutdown() throws ObjectStoreException {}

    public void startTransaction() {
        LOG.debug("start transaction");
    }
}

/*
 * Naked Objects - a framework that exposes behaviourally complete business objects directly to the
 * user. Copyright (C) 2000 - 2003 Naked Objects Group Ltd
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 * 
 * The authors can be contacted via www.nakedobjects.org (the registered address of Naked Objects
 * Group is Kingsway House, 123 Goldworth Road, Woking GU21 1NR, UK).
 */

