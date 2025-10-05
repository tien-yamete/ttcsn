package com.tien.identityservice.constant;

public class EmailTemplate {
    public static String welcomeEmail(String username) {
        return String.format("""
            <html>
            <body style="margin:0;padding:0;background:#f6f7fb;color:#2b2f38;font-family:'Helvetica Neue',Arial,sans-serif;">
                <table role="presentation" width="100%%" cellpadding="0" cellspacing="0" style="padding:32px 0;background:#f6f7fb;">
                <tr>
                    <td align="center">
                    <table role="presentation" width="640" cellpadding="0" cellspacing="0" style="background:#fff;border-radius:14px;box-shadow:0 8px 24px rgba(0,0,0,0.06);overflow:hidden;">
                        <tr>
                        <td align="center" style="padding:28px 20px;background:linear-gradient(135deg,#28c76f,#1e9e45);">
                            <h1 style="margin:0;color:#fff;font-size:24px;font-weight:800;letter-spacing:.2px;">Welcome to Friendify 🎉</h1>
                        </td>
                        </tr>
                        <tr>
                        <td style="padding:28px 32px;">
                            <p style="margin:0 0 10px 0;font-size:16px;">Hello <strong style="color:#1e9e45;">%s</strong>,</p>
                            <p style="margin:0 0 18px 0;font-size:15px;line-height:1.6;">
                            You’re in! Start building real connections and discover communities on <strong>Friendify</strong>.
                            </p>
                            <a href="https://friendify.com/start" style="display:inline-block;background:#1e9e45;color:#fff;text-decoration:none;padding:12px 20px;border-radius:10px;font-weight:700;">
                            Get Started →
                            </a>
                        </td>
                        </tr>
                        <tr>
                        <td align="center" style="padding:16px 24px;background:#fafbfc;font-size:12px;color:#8a909b;">
                            © 2025 Friendify Inc · <a href="https://friendify.com/privacy" style="color:#5aaf83;text-decoration:none;">Privacy</a> · <a href="https://friendify.com/terms" style="color:#5aaf83;text-decoration:none;">Terms</a>
                        </td>
                        </tr>
                    </table>
                    <p style="max-width:640px;margin:10px 16px 0;font-size:12px;color:#9aa1ad;">
                        Can’t click the button? Open: <a href="http://localhost:3000/" style="color:#1e9e45;text-decoration:none;">http://localhost:3000/</a>
                    </p>
                    </td>
                </tr>
                </table>
            </body>
            </html>
            """, username);
    }

    public static String otpEmail(String username, String otp) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background: #f6f7fb; padding: 20px;">
                <div style="max-width:600px;margin:auto;background:white;border-radius:10px;padding:20px;box-shadow:0 4px 10px rgba(0,0,0,0.1);">
                    <h2 style="color:#1e9e45;">Email Verification</h2>
                    <p>Hello <b>%s</b>,</p>
                    <p>Your OTP code is:</p>
                    <h1 style="color:#1e9e45;">%s</h1>
                    <p>This code will expire in <b>5 minutes</b>.</p>
                </div>
            </body>
            </html>
            """, username, otp);
    }

    public static String resetPasswordEmail(String username, String otp) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; background: #f6f7fb; padding: 20px;">
                <div style="max-width:600px;margin:auto;background:white;border-radius:10px;padding:20px;box-shadow:0 4px 10px rgba(0,0,0,0.1);">
                    <h2 style="color:#e74c3c;">Reset Password</h2>
                    <p>Hello <b>%s</b>,</p>
                    <p>We received a request to reset your password. Use the OTP below:</p>
                    <h1 style="color:#e74c3c;">%s</h1>
                    <p>This code will expire in <b>5 minutes</b>. If you didn’t request this, ignore this email.</p>
                </div>
            </body>
            </html>
            """, username, otp);
    }
}
