# Social Media Web Application

A modern, feature-rich social media application built with React, Vite, and Material-UI. This application provides a complete social networking experience with a clean, responsive design that works seamlessly across all devices.

## 🎯 Features

### Core Features

#### 1. **Fixed Top Navigation Bar**
- Editable logo with home navigation
- Smart search bar with real-time suggestions
  - User suggestions
  - Trending topics
  - Recent searches
- Message icon with unread badge
- Notification icon with count badge
- User avatar dropdown menu with:
  - Profile preview
  - Settings & Privacy
  - Dark/Light mode toggle
  - Sign Out option

#### 2. **Left Sidebar Navigation**
- Vertical navigation menu with icons:
  - News Feed (Home)
  - Messages
  - Friends
  - Groups
  - Pages
  - Marketplace
  - Saved Items
- Active state highlighting with gradient background
- Fully collapsible on mobile devices
- Smooth transitions and hover effects

#### 3. **Main Feed Area**
- **Create Post Composer**
  - User avatar
  - Text input field
  - Media upload buttons (Photo, Video, Feeling/Activity)
  - Opens full composer modal with:
    - Multi-line text editor
    - Media upload with previews
    - Support for up to 8 images/videos
    - File size validation (max 25MB per file)
    - Cancel and Post actions

- **Post Cards**
  - Author avatar with online status indicator
  - Username and post timestamp
  - Privacy indicator (Public/Friends/Private)
  - Post content with rich formatting
  - Image/Video grid display
  - Reaction system:
    - Quick like (tap)
    - Long-press for reaction picker (Like, Love, Haha, Wow, Sad, Angry)
    - Animated reaction feedback
  - Comment system:
    - Expandable comments section
    - Real-time comment submission
    - Reply functionality
    - Like on comments
  - Share functionality
  - Edit/Delete options for own posts
  - Statistics (reaction count, comment count)

#### 4. **Right Sidebar** (Hidden on tablets and mobile)
- **Friend Suggestions**
  - User avatars
  - Mutual friends count
  - Add/Remove actions
  
- **Trending Topics**
  - Hashtag listings
  - Post count for each topic
  - "Trending" badge
  - Quick navigation to topic

- **Upcoming Events**
  - Event title, date, and time
  - Interest count
  - Quick "Interested" action
  - Event preview cards

#### 5. **Enhanced Profile Page**
- **Cover Photo**
  - Upload/Edit functionality
  - Full-width responsive display
  
- **Profile Header**
  - Large avatar with edit option
  - Upload progress indicator
  - Name and username
  - Quick stats (Friends, Photos, Posts)
  - Action buttons:
    - Add Friend
    - Message
    - More options
  
- **Profile Tabs**
  - **Posts**: All user posts in timeline
  - **About**: 
    - Work information
    - Education
    - Location
    - Birthday
    - Relationship status
    - Edit mode for updating info
  - **Friends**: Grid view of all friends with avatars
  - **Photos**: Gallery view of all photos

#### 6. **Messages/Chat Page**
- **Two-Panel Layout**
  - Left: Conversation list
    - Search conversations
    - Unread message badges
    - Last message preview
    - Timestamp
    - Create new chat button
  
  - Right: Active chat panel
    - Message bubbles (sender/receiver)
    - Avatar display
    - Timestamp for each message
    - Typing indicator
    - Send button
    - Optimistic UI updates
    - Failed message retry

- **New Chat Creation**
  - User search and selection
  - Instant conversation creation

#### 7. **Friends Page**
- **Three Tabs**:
  - **Friend Requests**: Accept/Decline invitations with mutual friends count
  - **Suggestions**: Discover new friends with reasons (mutual friends, location, work, school)
  - **All Friends**: Complete friends list with search functionality

- Features:
  - Accept/Decline friend requests
  - Send friend requests
  - Unfriend functionality
  - Message friends directly
  - Real-time updates with snackbar notifications

#### 8. **Groups Page**
- Group listings with:
  - Group avatar
  - Member count
  - Activity indicators
  - Join/Leave options
- Group details view
- Create new group functionality

#### 9. **Pages**
- Discover and follow pages
- **Two Tabs**:
  - Your Pages: Pages you follow
  - Discover: Find new pages
- Page details:
  - Verified badges
  - Follower counts
  - Category tags
  - Follow/Unfollow actions
- Create new page option
- Suggested pages section

#### 10. **Marketplace**
- Product listings with:
  - Product images
  - Prices
  - Location
  - Seller information
  - Category tags
- Features:
  - Search functionality
  - Category filtering
  - Favorite/Save items
  - View details
  - Responsive grid layout

#### 11. **Saved Items**
- **Content Types**:
  - Articles
  - Videos
  - Links
  - Images
- Features:
  - Filter by type (tabs)
  - Category filtering
  - Remove saved items
  - Preview thumbnails
  - Timestamp of when saved

#### 12. **Login/Register Pages**
- **Modern Split-Screen Design**
  - Left: Branding/Welcome panel with gradient
  - Right: Form panel
  
- **Login Features**:
  - Email/Username input
  - Password with show/hide toggle
  - Forgot password link
  - Google Sign-In placeholder
  - "Create account" link
  - Form validation
  - Error handling with snackbars

- **Register Features**:
  - Multi-step registration
  - Email verification
  - OTP verification
  - Password strength indicator

#### 13. **Dark/Light Mode**
- Toggle in user menu
- Persistent preference (localStorage)
- System preference detection
- Smooth color transitions
- All components fully themed
- Optimized contrast ratios

## 🎨 Design Highlights

### Color Scheme
- **Primary Gradient**: Purple to Blue (`#667eea` to `#764ba2`)
- **Neutral Colors**: Modern gray scale
- **Clean Typography**: System fonts for optimal readability
- **Semantic Colors**: Success (green), Error (red), Warning (orange), Info (blue)

### Responsive Design
- **Mobile-First Approach**
- **Breakpoints**:
  - xs: 0px (mobile)
  - sm: 600px (tablet)
  - md: 900px (small desktop)
  - lg: 1200px (large desktop)
  - xl: 1536px (extra large)

### Component Architecture
- **Reusable Components**:
  - `Header.jsx` - Top navigation
  - `SideMenu.jsx` - Left sidebar
  - `RightSidebar.jsx` - Right sidebar
  - `Post.jsx` - Post card
  - `CreatePostComposer.jsx` - New post widget
  - `MediaUpload.jsx` - File upload with previews
  - `NewChatPopover.jsx` - Chat creation dialog
  - `Scene.jsx` - Layout wrapper

- **Page Components**:
  - `Home.jsx` - Main feed
  - `ProfileEnhanced.jsx` - User profile
  - `ChatPage.jsx` - Messaging
  - `FriendsPage.jsx` - Friends management
  - `GroupPage.jsx` - Groups
  - `Marketplace.jsx` - Marketplace
  - `Pages.jsx` - Pages
  - `Saved.jsx` - Saved items
  - `Login.jsx` / `Register.jsx` - Authentication

## 🛠️ Technology Stack

- **React 18.3.1** - UI library
- **Vite 5.1.3** - Build tool and dev server
- **Material-UI (MUI) 5.15** - Component library
  - `@mui/material` - Core components
  - `@mui/icons-material` - Icon set
  - `@mui/x-date-pickers` - Date picker
- **Emotion** - CSS-in-JS styling
- **React Router DOM 6.23** - Client-side routing
- **Axios 1.7** - HTTP client
- **Day.js 1.11** - Date formatting

## 📦 Project Structure

```
web-app/
├── public/
│   ├── logo/
│   │   └── logo.png
│   └── robots.txt
├── src/
│   ├── components/
│   │   ├── Header.jsx
���   │   ├── SideMenu.jsx
│   │   ├── RightSidebar.jsx
│   │   ├── Post.jsx
│   │   ├── CreatePostComposer.jsx
│   │   ├── MediaUpload.jsx
│   │   ├── NewChatPopover.jsx
│   │   ├── LoginLeftPanel.jsx
│   │   └── SendOtpButton.jsx
│   ├── pages/
│   │   ├── Home.jsx
│   │   ├── ProfileEnhanced.jsx
│   │   ├── Profile.jsx (simple version)
│   │   ├── ChatPage.jsx
│   │   ├── FriendsPage.jsx
│   │   ├── GroupPage.jsx
│   │   ├── GroupDetail.jsx
│   │   ├── Marketplace.jsx
│   │   ├── Pages.jsx
│   │   ├── Saved.jsx
│   │   ├── Login.jsx
│   │   ├── Register.jsx
│   │   ├── ForgotPassword.jsx
│   │   ├── ResetPassword.jsx
│   │   ├── VerifyOtpPage.jsx
│   │   ├── Settings.jsx
│   │   ├── SearchPage.jsx
│   │   └── Scene.jsx (layout wrapper)
│   ├── routes/
│   │   └── AppRoutes.jsx
│   ├── services/
│   │   ├── authenticationService.js
│   │   ├── localStorageService.js
│   │   ├── postService.js
│   │   └── userService.js
│   ├── configurations/
│   │   ├── configuration.js
│   │   └��─ httpClient.js
│   ├── App.jsx
│   ├── index.jsx
│   └── index.css
├── package.json
├── vite.config.mjs
└── README.md
```

## 🚀 Getting Started

### Prerequisites
- Node.js 16+ and npm/yarn

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd web-app
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

4. Open your browser to:
```
http://localhost:5173
```

### Build for Production

```bash
npm run build
```

The optimized build will be in the `dist` folder.

### Preview Production Build

```bash
npm run preview
```

## 🔧 Configuration

### Environment Variables
Create a `.env` file in the root directory:

```env
VITE_API_BASE_URL=your_api_url_here
```

### API Integration
The app uses the following services (see `src/services/`):
- `authenticationService.js` - Login, logout, authentication checks
- `postService.js` - Create, edit, delete posts
- `userService.js` - User profile, avatar upload
- `httpClient.js` - Axios instance with interceptors

## 🎨 Customization

### Theme
Edit `src/App.jsx` to customize the theme:

```javascript
const theme = createTheme({
  palette: {
    mode,
    primary: {
      main: '#667eea', // Your primary color
    },
    // ... other theme settings
  },
});
```

### Logo
Replace `/public/logo/logo.png` with your own logo.

### Colors
The app uses a gradient theme that can be customized in component styles:
```javascript
background: "linear-gradient(135deg, #667eea 0%, #764ba2 100%)"
```

## 📱 Responsive Behavior

### Mobile (< 600px)
- Hamburger menu for sidebar
- Hidden right sidebar
- Single column layout
- Optimized touch targets
- Swipe gestures for navigation

### Tablet (600px - 900px)
- Collapsible sidebar
- Hidden right sidebar
- Two-column layout
- Optimized spacing

### Desktop (> 900px)
- Fixed left sidebar
- Visible right sidebar (on screens > 1200px)
- Three-column layout
- Full feature set

## 🧩 Key Features Detail

### Search Suggestions
The search bar provides intelligent suggestions:
- Recent searches (with history icon)
- User profiles (with avatars)
- Trending topics (with trend icon and post counts)

### Post Reactions
Advanced reaction system:
- **Tap**: Quick like
- **Long press**: Reaction picker with 6 emotions
- **Animated**: Smooth animations on selection
- **Count display**: Shows total reactions with emoji

### Media Upload
Robust file handling:
- Drag & drop support
- Multiple file selection (up to 8 files)
- File type validation (images/videos)
- Size validation (max 25MB per file)
- Preview with thumbnails
- Remove individual files
- Progress indicators

### Real-time Updates
- Optimistic UI updates for instant feedback
- Loading states for async operations
- Error handling with retry options
- Success/Error notifications via Snackbars

## 🔐 Security Features

- JWT token-based authentication
- Secure password input with visibility toggle
- CSRF protection
- XSS prevention
- Input sanitization
- Secure HTTP-only cookies (backend dependent)

## ♿ Accessibility

- ARIA labels on interactive elements
- Keyboard navigation support
- Focus management
- High contrast mode support
- Screen reader friendly
- Semantic HTML structure

## 🧪 Testing

Run tests (when configured):
```bash
npm test
```

## 📄 License

This project is private and proprietary.

## 🤝 Contributing

This is a private project. Contact the maintainers for contribution guidelines.

## 📞 Support

For issues or questions, please contact the development team.

## 🎯 Roadmap

Future enhancements:
- [ ] Stories feature
- [ ] Live video streaming
- [ ] Voice/Video calls in chat
- [ ] Advanced search filters
- [ ] Emoji reactions on comments
- [ ] Share to external platforms
- [ ] Advanced privacy controls
- [ ] Two-factor authentication
- [ ] Mobile app (React Native)
- [ ] Progressive Web App (PWA) features

## 🙏 Acknowledgments

- Material-UI team for the excellent component library
- React team for the amazing framework
- Vite team for the blazing-fast build tool
- All open-source contributors

---

**Built with ❤️ using React + Vite + Material-UI**
