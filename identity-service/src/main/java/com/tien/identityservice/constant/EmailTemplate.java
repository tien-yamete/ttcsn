package com.tien.identityservice.constant;

/**
 * EmailTemplate: Class chứa các template HTML cho email.
 * - welcomeEmail: Email chào mừng sau khi xác thực email thành công
 * - otpEmail: Email gửi mã OTP để xác thực email
 * - resendVerificationEmail: Email gửi lại mã OTP khi user yêu cầu
 */
public class EmailTemplate {
    public static String welcomeEmail(String username) {
        return String.format(
                """
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
			""",
                username);
    }

    public static String otpEmail(String username, String otpCode) {
        return String.format(
                """
		<html>
		<body style="margin:0;padding:0;background:#f6f7fb;color:#2b2f38;
					font-family:'Helvetica Neue',Arial,sans-serif;">
			<table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
				style="padding:32px 0;background:#f6f7fb;">
				<tr>
					<td align="center">
						<table role="presentation" width="640" cellpadding="0" cellspacing="0"
							style="background:#fff;border-radius:14px;
							box-shadow:0 8px 24px rgba(0,0,0,0.06);overflow:hidden;">

							<!-- Header -->
							<tr>
								<td align="center"
									style="padding:28px 20px;
										background:linear-gradient(135deg,#28c76f,#1e9e45);">
									<h1 style="margin:0;color:#fff;font-size:24px;
											font-weight:800;letter-spacing:.2px;">
										Welcome to Friendify 🎉
									</h1>
								</td>
							</tr>

							<!-- Body -->
							<tr>
								<td style="padding:28px 32px;text-align:center;">
									<p style="margin:0 0 10px 0;font-size:16px;">
										Hello <strong style="color:#1e9e45;">%s</strong>,
									</p>
									<p style="margin:0 0 18px 0;font-size:15px;line-height:1.6;">
										Welcome to <strong>Friendify</strong>!
										Here’s your verification code to get started:
									</p>
									<div style="display:inline-block;background:#f1fdf6;
												padding:12px 24px;border-radius:8px;
												font-size:22px;font-weight:700;
												color:#1e9e45;letter-spacing:3px;
												margin:12px 0 18px;">
										%s
									</div>
									<p style="font-size:13px;color:#8a909b;">
										This code will expire in 15 minutes.
										Please do not share it with anyone.
									</p>
								</td>
							</tr>

							<!-- Footer -->
							<tr>
								<td align="center"
									style="padding:16px 24px;background:#fafbfc;
										font-size:12px;color:#8a909b;">
									© 2025 Friendify Inc ·
									<a href="https://friendify.com/privacy"
									style="color:#5aaf83;text-decoration:none;">Privacy</a> ·
									<a href="https://friendify.com/terms"
									style="color:#5aaf83;text-decoration:none;">Terms</a>
								</td>
							</tr>
						</table>
						<p style="max-width:640px;margin:10px 16px 0;font-size:12px;color:#9aa1ad;">
							Can’t use the code? Contact us at
							<a href="mailto:support@friendify.com"
							style="color:#1e9e45;text-decoration:none;">
							support@friendify.com
							</a>
						</p>
					</td>
				</tr>
			</table>
		</body>
		</html>
		""",
                username, otpCode);
    }

    public static String resendVerificationEmail(String username, String otpCode) {
        return String.format(
                """
		<html>
		<body style="margin:0;padding:0;background:#f6f7fb;color:#2b2f38;
					font-family:'Helvetica Neue',Arial,sans-serif;">
			<table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
				style="padding:32px 0;background:#f6f7fb;">
				<tr>
					<td align="center">
						<table role="presentation" width="640" cellpadding="0" cellspacing="0"
							style="background:#fff;border-radius:14px;
							box-shadow:0 8px 24px rgba(0,0,0,0.06);overflow:hidden;">

							<!-- Header -->
							<tr>
								<td align="center"
									style="padding:28px 20px;
										background:linear-gradient(135deg,#28c76f,#1e9e45);">
									<h1 style="margin:0;color:#fff;font-size:24px;
											font-weight:800;letter-spacing:.2px;">
										Friendify Verification Code 🔑
									</h1>
								</td>
							</tr>

							<!-- Body -->
							<tr>
								<td style="padding:28px 32px;text-align:center;">
									<p style="margin:0 0 10px 0;font-size:16px;">
										Hi <strong style="color:#1e9e45;">%s</strong>,
									</p>
									<p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;">
										Here’s your new verification code.
										Use it within <strong>15 minutes</strong> to verify your email.
									</p>
									<div style="display:inline-block;background:#f1fdf6;
												padding:12px 24px;border-radius:8px;
												font-size:22px;font-weight:700;
												color:#1e9e45;letter-spacing:3px;
												margin:12px 0 18px;">
										%s
									</div>
									<p style="font-size:13px;color:#8a909b;">
										Didn’t request a new code? Just ignore this email.
									</p>
								</td>
							</tr>

							<!-- Footer -->
							<tr>
								<td align="center"
									style="padding:16px 24px;background:#fafbfc;
										font-size:12px;color:#8a909b;">
									© 2025 Friendify Inc ·
									<a href="https://friendify.com/privacy"
									style="color:#5aaf83;text-decoration:none;">Privacy</a> ·
									<a href="https://friendify.com/terms"
									style="color:#5aaf83;text-decoration:none;">Terms</a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</body>
		</html>
		""",
                username, otpCode);
    }

    public static String resetPasswordEmail(String username, String otpCode) {
        return String.format(
                """
		<html>
		<body style="margin:0;padding:0;background:#f6f7fb;color:#2b2f38;
					font-family:'Helvetica Neue',Arial,sans-serif;">
			<table role="presentation" width="100%%" cellpadding="0" cellspacing="0"
				style="padding:32px 0;background:#f6f7fb;">
				<tr>
					<td align="center">
						<table role="presentation" width="640" cellpadding="0" cellspacing="0"
							style="background:#fff;border-radius:14px;
							box-shadow:0 8px 24px rgba(0,0,0,0.06);overflow:hidden;">

							<!-- Header -->
							<tr>
								<td align="center"
									style="padding:28px 20px;
										background:linear-gradient(135deg,#dc3545,#c82333);">
									<h1 style="margin:0;color:#fff;font-size:24px;
											font-weight:800;letter-spacing:.2px;">
										Reset Password 🔒
									</h1>
								</td>
							</tr>

							<!-- Body -->
							<tr>
								<td style="padding:28px 32px;text-align:center;">
									<p style="margin:0 0 10px 0;font-size:16px;">
										Hi <strong style="color:#c82333;">%s</strong>,
									</p>
									<p style="margin:0 0 20px 0;font-size:15px;line-height:1.6;">
										We received a request to reset your password.
										Use the code below to reset your password:
									</p>
									<div style="display:inline-block;background:#ffe6e6;
												padding:12px 24px;border-radius:8px;
												font-size:22px;font-weight:700;
												color:#c82333;letter-spacing:3px;
												margin:12px 0 18px;">
										%s
									</div>
									<p style="font-size:13px;color:#8a909b;">
										This code will expire in 15 minutes.
										If you didn't request this, please ignore this email.
									</p>
								</td>
							</tr>

							<!-- Footer -->
							<tr>
								<td align="center"
									style="padding:16px 24px;background:#fafbfc;
										font-size:12px;color:#8a909b;">
									© 2025 Friendify Inc ·
									<a href="https://friendify.com/privacy"
									style="color:#c82333;text-decoration:none;">Privacy</a> ·
									<a href="https://friendify.com/terms"
									style="color:#c82333;text-decoration:none;">Terms</a>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</table>
		</body>
		</html>
		""",
                username, otpCode);
    }
}
