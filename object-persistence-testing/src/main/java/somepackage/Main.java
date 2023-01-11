package somepackage;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.common.SessionProvider;

public class Main {
    public static void main(String[] args) {
        try (Session session = SessionProvider.getInstance().createSession()) {
            System.out.println(session.createTable(Table.class).get());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        try (Session session = SessionProvider.getInstance().createSession()) {
            System.out.println(session.createTable(Table.class).get());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        SessionProvider.getInstance().shutdown();
    }
}
