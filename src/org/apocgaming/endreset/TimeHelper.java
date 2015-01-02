package org.apocgaming.endreset;

/**
 * Created by Haze on 1/2/2015.
 */
    public class TimeHelper {
        private long lastMS = -1L;

        public boolean isDelayComplete(float milliseconds) {
            return (float) (getCurrentMS() - this.lastMS) >= milliseconds;
        }

        public void setLastMS(long last) {
            this.lastMS = last;
        }

        public short convertToMS(float perSecond) {
            return (short) (int) (1000 / perSecond);
        }

        public long getDifference() {
            return getCurrentMS() - this.lastMS;
        }

        public long getLastMS() {
            return this.lastMS;
        }

        public long getCurrentMS() {
            return System.nanoTime() / 1000000;
        }
}
