import 'package:flutter/material.dart';

class ChatInputBar extends StatelessWidget {
  const ChatInputBar({
    required this.message,
    required this.isSending,
    required this.onMessageChange,
    required this.onMicClick,
    required this.onSendClick,
    super.key,
  });

  final String message;
  final bool isSending;
  final ValueChanged<String> onMessageChange;
  final VoidCallback onMicClick;
  final VoidCallback onSendClick;

  @override
  Widget build(BuildContext context) {
    return ColoredBox(
      color: Colors.white,
      child: Padding(
        padding: const EdgeInsets.all(8),
        child: Row(
          children: [
            Expanded(
              child: TextField(
                controller: TextEditingController(text: message)
                  ..selection = TextSelection.collapsed(offset: message.length),
                onChanged: onMessageChange,
                minLines: 1,
                maxLines: null,
                decoration: InputDecoration(
                  hintText: 'Kuch to bol',
                  filled: true,
                  fillColor: Colors.grey.shade300,
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(24),
                    borderSide: BorderSide.none,
                  ),
                ),
              ),
            ),
            const SizedBox(width: 8),
            _CircleIconButton(
              color: const Color(0xFF1D4760),
              icon: Icons.mic,
              onPressed: isSending ? null : onMicClick,
            ),
            const SizedBox(width: 8),
            _CircleIconButton(
              color: const Color(0xFF128C7E),
              icon: Icons.send,
              isBusy: isSending,
              onPressed:
                  isSending || message.trim().isEmpty ? null : onSendClick,
            ),
          ],
        ),
      ),
    );
  }
}

class _CircleIconButton extends StatelessWidget {
  const _CircleIconButton({
    required this.color,
    required this.icon,
    required this.onPressed,
    this.isBusy = false,
  });

  final Color color;
  final IconData icon;
  final VoidCallback? onPressed;
  final bool isBusy;

  @override
  Widget build(BuildContext context) {
    return SizedBox.square(
      dimension: 48,
      child: IconButton.filled(
        style: IconButton.styleFrom(
          backgroundColor: onPressed == null ? Colors.grey.shade300 : color,
          foregroundColor: Colors.white,
        ),
        onPressed: onPressed,
        icon: isBusy
            ? const SizedBox.square(
                dimension: 20,
                child: CircularProgressIndicator(
                    strokeWidth: 2, color: Colors.white),
              )
            : Icon(icon),
      ),
    );
  }
}
