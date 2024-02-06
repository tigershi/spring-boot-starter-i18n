package org.springframework.boot.i18n.model;

public class L10nException extends Exception{

        private static final long serialVersionUID = -4408979501427331605L;

        public L10nException() {
            super();
        }

        public L10nException(String message) {
            super(message);
        }

        public L10nException(String msg, Throwable throwable) {
            super(msg, throwable);
        }
}
