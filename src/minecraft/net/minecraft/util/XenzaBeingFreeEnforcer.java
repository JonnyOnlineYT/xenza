package net.minecraft.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.security.MessageDigest;

public class XenzaBeingFreeEnforcer {
    public static String getMethodHash(String className, String methodName) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass ctClass = classPool.get(className);
            CtMethod ctMethod = ctClass.getDeclaredMethod(methodName);

            // Get the bytecode as bytes
            byte[] bytecode = ctMethod.getMethodInfo().getCodeAttribute().getCode();

            // Calculate the hash of the bytecode
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(bytecode);

            // Convert the hash bytes to a hexadecimal string
            StringBuilder hashBuilder = new StringBuilder();
            for (byte b : hashBytes) {
                hashBuilder.append(String.format("%02x", b));
            }
            return hashBuilder.toString();
            // Compare with the stored hash
            //return calculatedHash.equals(storedHash);
        } catch (Exception e) {
            System.exit("fuck".hashCode());
        }
        return "this doesnt get reached LOL";
    }
}
