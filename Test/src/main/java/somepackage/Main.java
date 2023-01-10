package somepackage;

import com.github.object.persistence.api.session.Session;
import com.github.object.persistence.common.SessionProvider;

public class Main {
    public static void main(String[] args) {
        try (Session session = SessionProvider.getInstance().createSession()) {
            session.createTable(Table.class).get();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
