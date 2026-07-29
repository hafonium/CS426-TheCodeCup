import socket
import smtplib
from email.mime.multipart import MIMEMultipart
from email.mime.text import MIMEText
from email.utils import formataddr

from app.core.config import settings

def send_otp_email(otp_code: str, email: str) -> bool:
    message = MIMEMultipart("alternative")
    message["Subject"] = "Your Verification Code"
    sender_name = "The Code Cup"
    message["From"] = formataddr((sender_name, settings.SENDER_EMAIL))
    message["To"] = email

    text = f"Your verification code is: {otp_code}. It will expire shortly."
    html = f"""
    <html>
      <body>
        <h2>Your Verification Code</h2>
        <p>Use the code below to complete your verification:</p>
        <h1 style="color: #4CAF50; letter-spacing: 2px;">{otp_code}</h1>
        <p>If you did not request this, please ignore this email.</p>
      </body>
    </html>
    """

    message.attach(MIMEText(text, "plain"))
    message.attach(MIMEText(html, "html"))

    try:
        # Resolve hostname explicitly to IPv4 to prevent IPv6 [Errno 101] in Docker
        ip_address = socket.gethostbyname(settings.SMTP_SERVER)

        with smtplib.SMTP(ip_address, settings.SMTP_PORT, timeout=10) as server:
            # Pass original hostname for TLS SNI validation
            server.starttls()
            server.login(settings.SENDER_EMAIL, settings.SENDER_PASSWORD)
            server.sendmail(settings.SENDER_EMAIL, email, message.as_string())
        return True
    except Exception as e:
        print(f"Failed to send email: {e}")
        return False