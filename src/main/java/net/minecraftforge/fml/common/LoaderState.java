/*
 * Forge Mod Loader
 * Copyright (c) 2012-2013 cpw.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     cpw - implementation
 */

package net.minecraftforge.fml.common;

import com.google.common.base.Throwables;
import net.minecraftforge.fml.common.event.*;

/**
 * The state enum used to help track state progression for the loader
 *
 * @author cpw
 */
public enum LoaderState {
    NOINIT("Uninitialized", null),
    LOADING("Loading", null),
    CONSTRUCTING("Constructing mods", FMLConstructionEvent.class),
    PREINITIALIZATION("Pre-initializing mods", FMLPreInitializationEvent.class),
    INITIALIZATION("Initializing mods", FMLInitializationEvent.class),
    POSTINITIALIZATION("Post-initializing mods", FMLPostInitializationEvent.class),
    AVAILABLE("Mod loading complete", FMLLoadCompleteEvent.class),
    SERVER_ABOUT_TO_START("Server about to start", FMLServerAboutToStartEvent.class),
    SERVER_STARTING("Server starting", FMLServerStartingEvent.class),
    SERVER_STARTED("Server started", FMLServerStartedEvent.class),
    SERVER_STOPPING("Server stopping", FMLServerStoppingEvent.class),
    SERVER_STOPPED("Server stopped", FMLServerStoppedEvent.class),
    ERRORED("Mod Loading errored", null);


    private final Class<? extends FMLStateEvent> eventClass;
    private final String name;

    LoaderState(String name, Class<? extends FMLStateEvent> event) {
        this.name = name;
        this.eventClass = event;
    }

    public LoaderState transition(boolean errored) {
        if (errored) {
            return ERRORED;
        }
        // stopping -> available
        if (this == SERVER_STOPPED) {
            return AVAILABLE;
        }
        return values()[ordinal() < values().length ? ordinal() + 1 : ordinal()];
    }

    public boolean hasEvent() {
        return eventClass != null;
    }

    public FMLStateEvent getEvent(Object... eventData) {
        try {
            return eventClass.getConstructor(Object[].class).newInstance((Object) eventData);
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    public LoaderState requiredState() {
        if (this == NOINIT) return NOINIT;
        return LoaderState.values()[this.ordinal() - 1];
    }

    public String getPrettyName() {
        return name;
    }

    public enum ModState {
        UNLOADED("Unloaded", "U"),
        LOADED("Loaded", "L"),
        CONSTRUCTED("Constructed", "C"),
        PREINITIALIZED("Pre-initialized", "H"),
        INITIALIZED("Initialized", "I"),
        POSTINITIALIZED("Post-initialized", "J"),
        AVAILABLE("Available", "A"),
        DISABLED("Disabled", "D"),
        ERRORED("Errored", "E");

        private final String label;
        private final String marker;

        ModState(String label, String marker) {
            this.label = label;
            this.marker = marker;
        }

        @Override
        public String toString() {
            return this.label;
        }

        public String getMarker() {
            return this.marker;
        }
    }
}
