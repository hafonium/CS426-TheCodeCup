from datetime import datetime, timezone, timedelta
from zoneinfo import ZoneInfo

def get_vn_now():
    # Gets Vietnam time, then strips timezone metadata (+07:00) 
    # so the database accepts 08:xx as plain numbers
    return datetime.now(ZoneInfo("Asia/Ho_Chi_Minh")).replace(tzinfo=None)