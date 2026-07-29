from sendgrid import SendGridAPIClient
from sendgrid.helpers.mail import Mail
from app.core.config import settings

def send_otp_email(otp_code: str, email: str) -> bool:
    html_content = f"""
    <html>
      <body>
        <h2>Your Verification Code</h2>
        <p>Use the code below to complete your verification:</p>
        <h1 style="color: #4CAF50; letter-spacing: 2px;">{otp_code}</h1>
        <p>If you did not request this, please ignore this email.</p>
      </body>
    </html>
    """

    message = Mail(
        from_email=(settings.SENDER_EMAIL, settings.SENDER_NAME),
        to_emails=email,
        subject="Your Verification Code",
        html_content=html_content
    )
    
    try:
        sg = SendGridAPIClient(settings.SENDGRID_API_KEY)
        response = sg.send(message)
        
        if response.status_code == 202:
            return True
        else:
            print(f"SendGrid returned unexpected status code: {response.status_code}")
            return False

    except Exception as e:
        print(f"Failed to send email via SendGrid API: {e}")
        return False