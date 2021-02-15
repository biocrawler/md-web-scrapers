package org.perpetualnetworks.mdcrawlerconsumer.database.session;

import org.hibernate.SessionFactory;
import org.perpetualnetworks.mdcrawlerconsumer.database.Database;

import javax.annotation.Nonnull;

public interface SessionFactoryStore {

    SessionFactory getSessionFactory(@Nonnull Database db);
}