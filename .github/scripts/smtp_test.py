#!/usr/bin/env python3
import smtplib
import os
import sys

def main():
    user = os.getenv('MAIL_USERNAME')
    pw = os.getenv('EMAIL_PASSWORD')
    to = os.getenv('EMAIL_TO')
    from_addr = os.getenv('EMAIL_FROM') or user
    if not user or not pw:
        print('MISSING_SECRETS - skipping SMTP test')
        return 0
    try:
        s = smtplib.SMTP('smtp.office365.com', 587, timeout=30)
        s.ehlo()
        s.starttls()
        try:
            res = s.login(user, pw)
            print('LOGIN_SUCCESS', res)
            s.sendmail(from_addr, [to], 'Subject: SMTP debug\n\nOK')
            print('SMTP SEND OK')
        except Exception as e:
            print('LOGIN_FAILED', repr(e))
            raise
        finally:
            try:
                s.quit()
            except Exception:
                pass
    except Exception as e:
        print('SMTP_CONNECTION_ERROR', repr(e))
        return 1
    print('SMTP_TEST_DONE')
    return 0

if __name__ == '__main__':
    sys.exit(main())
