package br.dbbackup.core;


@SuppressWarnings("serial")
public class DbbackupException extends Exception {
    public DbbackupException() {
        super();
    }

    public DbbackupException(String message) {
        super(message);
    }

    public DbbackupException(String message, Throwable cause) {
        super(message, cause);
    }

    public DbbackupException(Throwable cause) {
        super(cause);
    }
}
