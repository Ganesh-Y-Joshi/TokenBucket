import java.time.Instant;
import java.util.Objects;

public final class RequestWrapper {
        private final String ip;
        private Instant lastUpdatedTimeStamp;

        public RequestWrapper(String ip) {
            if (ip != null) {
                this.ip = ip;
                this.lastUpdatedTimeStamp = Instant.now();
            } else this.ip = "";
        }

        public Instant getLastUpdatedTimeStamp() {
            return lastUpdatedTimeStamp;
        }


        public String getIp() {
            return ip;
        }

        public void setLastUpdatedTimeStamp(Instant lastUpdatedTimeStamp) {
            this.lastUpdatedTimeStamp = lastUpdatedTimeStamp;
        }

    @Override
    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
        RequestWrapper that = (RequestWrapper) o;
        return this.ip.equals(that.ip);
    }


    @Override
        public int hashCode() {
            return Objects.hash(ip);
        }
    }

