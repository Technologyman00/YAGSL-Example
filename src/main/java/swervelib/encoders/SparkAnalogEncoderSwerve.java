package swervelib.encoders;

import com.revrobotics.CANSparkBase;
import com.revrobotics.REVLibError;
import com.revrobotics.SparkAnalogSensor;
import com.revrobotics.SparkAnalogSensor.Mode;
import java.util.function.Supplier;
import swervelib.motors.SwerveMotor;
import swervelib.telemetry.Alert;

/**
 * SPARK absolute encoder, attached through the data port analog pin.
 */
public class SparkAnalogEncoderSwerve extends SwerveAbsoluteEncoder
{

  /**
   * The {@link SparkAnalogSensor} representing the duty cycle encoder attached to the SPARK controller analog port.
   */
  public  SparkAnalogSensor encoder;
  /**
   * An {@link Alert} for if there is a failure configuring the encoder.
   */
  private Alert             failureConfiguring;
  /**
   * An {@link Alert} for if the absolute encoder does not support integrated offsets.
   */
  private Alert             doesNotSupportIntegratedOffsets;


  /**
   * Create the {@link SparkAnalogEncoderSwerve} object as a analog sensor from the {@link CANSparkBase} motor data
   * port analog pin.
   *
   * @param motor Motor to create the encoder from.
   */
  public SparkAnalogEncoderSwerve(SwerveMotor motor)
  {
    if (motor.getMotor() instanceof CANSparkBase)
    {
      encoder = ((CANSparkBase) motor.getMotor()).getAnalog(Mode.kAbsolute);
    } else
    {
      throw new RuntimeException("Motor given to instantiate SPARK Encoder is not a CANSparkBase");
    }
    failureConfiguring = new Alert(
        "Encoders",
        "Failure configuring SPARK Analog Encoder",
        Alert.AlertType.WARNING_TRACE);
    doesNotSupportIntegratedOffsets = new Alert(
        "Encoders",
        "SPARK Analog Sensors do not support integrated offsets",
        Alert.AlertType.WARNING_TRACE);

  }

  /**
   * Run the configuration until it succeeds or times out.
   *
   * @param config Lambda supplier returning the error state.
   */
  private void configureSpark(Supplier<REVLibError> config)
  {
    for (int i = 0; i < maximumRetries; i++)
    {
      if (config.get() == REVLibError.kOk)
      {
        return;
      }
    }
    failureConfiguring.set(true);
  }

  /**
   * Reset the encoder to factory defaults.
   */
  @Override
  public void factoryDefault()
  {
    // Do nothing
  }

  /**
   * Clear sticky faults on the encoder.
   */
  @Override
  public void clearStickyFaults()
  {
    // Do nothing
  }

  /**
   * Configure the absolute encoder to read from [0, 360) per second.
   *
   * @param inverted Whether the encoder is inverted.
   */
  @Override
  public void configure(boolean inverted)
  {
    encoder.setInverted(inverted);
  }

  /**
   * Get the absolute position of the encoder.
   *
   * @return Absolute position in degrees from [0, 360).
   */
  @Override
  public double getAbsolutePosition()
  {
    return encoder.getPosition();
  }

  /**
   * Get the instantiated absolute encoder Object.
   *
   * @return Absolute encoder object.
   */
  @Override
  public Object getAbsoluteEncoder()
  {
    return encoder;
  }

  /**
   * Sets the Absolute Encoder offset at the Encoder Level.
   *
   * @param offset the offset the Absolute Encoder uses as the zero point.
   * @return if setting Absolute Encoder Offset was successful or not.
   */
  @Override
  public boolean setAbsoluteEncoderOffset(double offset)
  {
    doesNotSupportIntegratedOffsets.set(true);
    return false;
  }

  /**
   * Get the velocity in degrees/sec.
   *
   * @return velocity in degrees/sec.
   */
  @Override
  public double getVelocity()
  {
    return encoder.getVelocity();
  }
}
