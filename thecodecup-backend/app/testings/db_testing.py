from sqlalchemy import text
from app.core.database import engine

def test_connection():
    try:
        # engine.connect() forces SQLAlchemy to establish a real connection to Neon
        with engine.connect() as connection:
            connection.execute(text("SELECT 1"))
            print("Database connection successful! You are talking to Neon Postgres.")
    except Exception as e:
        print("Database connection failed!")
        print(e)

if __name__ == "__main__":
    test_connection()