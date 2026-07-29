from pydantic_settings import BaseSettings, SettingsConfigDict

class Settings(BaseSettings):
    PROJECT_NAME: str = "The Code Cup API"
    # Fallback to a local SQLite database if the .env file is missing
    DATABASE_URL: str = "sqlite:///./thecodecup.db"
    # Security settings
    SECRET_KEY: str
    ALGORITHM: str
    ACCESS_TOKEN_EXPIRE_MINUTES: int
    MAX_OTP_ATTEMPTS: int

    # Email settings
    SMTP_SERVER: str
    SMTP_PORT: int
    SENDER_EMAIL: str
    SENDER_PASSWORD: str
    # SendGrid API Key for sending emails
    SENDGRID_API_KEY: str
    SENDER_EMAIL: str
    SENDER_NAME: str

    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

settings = Settings()