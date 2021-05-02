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

package net.minecraftforge.fml.common.asm.transformers;

import net.minecraft.launchwrapper.IClassNameTransformer;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.RemappingClassAdapter;

public class DeobfuscationTransformer implements IClassTransformer, IClassNameTransformer {

    private static final String[] EXEMPT_LIBS = new String[]{
        "com.google.",
        "com.mojang.",
        "joptsimple.",
        "io.netty.",
        "it.unimi.dsi.fastutil.",
        "oshi.",
        "com.sun.",
        "com.ibm.",
        "paulscode.",
        "com.jcraft"
    };

    private static final String[] EXEMPT_DEV = new String[]{
        "net.minecraft.",
        "net.minecraftforge."
    };

    private static final boolean RECALC_FRAMES = Boolean.parseBoolean(System.getProperty("FORGE_FORCE_FRAME_RECALC", "false"));
    private static final int WRITER_FLAGS = ClassWriter.COMPUTE_MAXS | (RECALC_FRAMES ? ClassWriter.COMPUTE_FRAMES : 0);
    private static final int READER_FLAGS = RECALC_FRAMES ? ClassReader.SKIP_FRAMES : ClassReader.EXPAND_FRAMES;
    // COMPUTE_FRAMES causes classes to be loaded, which could cause issues if the classes do not exist.
    // However in testing this has not happened. {As we run post SideTransfromer}
    // If reported we need to add a custom implementation of ClassWriter.getCommonSuperClass
    // that does not cause class loading.

    private final boolean deobfuscatedEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    @Override
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        if (!shouldTransform(name)) return bytes;

        ClassReader classReader = new ClassReader(bytes);
        ClassWriter classWriter = new ClassWriter(WRITER_FLAGS);
        RemappingClassAdapter remapAdapter = new FMLRemappingAdapter(classWriter);
        classReader.accept(remapAdapter, READER_FLAGS);
        return classWriter.toByteArray();
    }

    private boolean shouldTransform(String name) {
        boolean transformLib = true;
        for (String EXEMPT_LIB : EXEMPT_LIBS) {
            if (name.startsWith(EXEMPT_LIB)) {
                transformLib = false;
                break;
            }
        }

        if (deobfuscatedEnvironment) {
            if (!transformLib) return false;
            for (String s : EXEMPT_DEV) {
                if (name.startsWith(s)) {
                    return false;
                }
            }

            return true;
        } else {
            return transformLib;
        }
    }

    @Override
    public String remapClassName(String name) {
        return FMLDeobfuscatingRemapper.INSTANCE.map(name.replace('.', '/')).replace('/', '.');
    }

    @Override
    public String unmapClassName(String name) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(name.replace('.', '/')).replace('/', '.');
    }

}
