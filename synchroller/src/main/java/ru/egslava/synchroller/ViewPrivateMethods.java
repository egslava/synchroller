package ru.egslava.synchroller;

import android.view.View;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Special class for easy access to some private methods of View
 */
public class ViewPrivateMethods {

        private Method cHSR, cVSR, cHSE, cVSE;
        View view;

        ViewPrivateMethods(){
            try {
                cHSR = View.class.getDeclaredMethod("computeHorizontalScrollRange");
                cHSE = View.class.getDeclaredMethod("computeHorizontalScrollExtent");
                cVSR = View.class.getDeclaredMethod("computeVerticalScrollRange");
                cVSE = View.class.getDeclaredMethod("computeVerticalScrollExtent");

                cHSR.setAccessible(true);
                cHSE.setAccessible(true);
                cVSR.setAccessible(true);
                cVSE.setAccessible(true);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);          //impossible
            }
        }

        int computeHorizontalScrollRange(){
            return (Integer)unsafeCall(cHSR, view);
        }
        int computeHorizontalScrollExtent(){
            return (Integer)unsafeCall(cHSE, view);
        }
        int computeVerticalScrollRange(){
            return (Integer)unsafeCall(cVSR, view);
        }
        int computeVerticalScrollExtent(){
            return (Integer)unsafeCall(cVSE, view);
        }

        protected static Object unsafeCall(Method method, View subject, Object... args){
            try {
                return method.invoke(subject, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);      // seems to be impossible
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);      // seems to be impossible
            }
        }
    }
